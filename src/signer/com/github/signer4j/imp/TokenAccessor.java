/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package com.github.signer4j.imp;

import static com.github.signer4j.IFilePath.toPaths;
import static com.github.signer4j.imp.DeviceCertificateEntry.toEntries;
import static com.github.signer4j.imp.exception.InterruptedSigner4JRuntimeException.lambda;
import static com.github.signer4j.imp.exception.InterruptedSigner4JRuntimeException.of;
import static com.github.utils4j.gui.imp.SwingTools.invokeAndWait;
import static com.github.utils4j.gui.imp.SwingTools.isTrue;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.github.signer4j.IAuthStrategy;
import com.github.signer4j.ICertificate;
import com.github.signer4j.ICertificateListUI.ICertificateEntry;
import com.github.signer4j.ICertificateListUI.IChoice;
import com.github.signer4j.IDevice;
import com.github.signer4j.IDeviceManager;
import com.github.signer4j.IDriverVisitor;
import com.github.signer4j.IFilePath;
import com.github.signer4j.IStatusMonitor;
import com.github.signer4j.IToken;
import com.github.signer4j.ITokenAccess;
import com.github.signer4j.TokenType;
import com.github.signer4j.gui.CertificateListDialog;
import com.github.signer4j.gui.alert.ExpiredPasswordAlert;
import com.github.signer4j.gui.alert.NoTokenPresentAlert;
import com.github.signer4j.gui.alert.TokenLockedAlert;
import com.github.signer4j.gui.utils.InvalidPinAlert;
import com.github.signer4j.imp.exception.ExpiredCredentialException;
import com.github.signer4j.imp.exception.InvalidPinException;
import com.github.signer4j.imp.exception.LoginCanceledException;
import com.github.signer4j.imp.exception.NoTokenPresentException;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.signer4j.imp.exception.TokenLockedException;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.function.IBiProcedure;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public abstract class TokenAccessor<T extends IToken> implements ITokenAccess<T> {
  
  private class FilePathStrategy extends AbstractStrategy {
    @Override
    public void lookup(IDriverVisitor visitor) {
      a3Libraries.forEach(fp -> createAndVisit(Paths.get(fp.getPath()), visitor));
    }
  }
  
  private volatile T token;

  private IAuthStrategy strategy;
  
  private final IDeviceManager manager;

  private volatile boolean autoForce = true;

  private volatile List<IFilePath> a1Files = new ArrayList<>();
  
  private volatile List<IFilePath> a3Libraries = new ArrayList<>();

  private BehaviorSubject<IStatusMonitor> tokenCycle = BehaviorSubject.create();
  
  protected static interface IFileLoader extends IBiProcedure<List<IFilePath>, List<IFilePath>> {};
  
  protected TokenAccessor(AuthStrategy strategy, IFileLoader loader) {
    this.strategy = Args.requireNonNull(strategy, "strategy is null");
    loader.call(a1Files, a3Libraries);
    this.manager = new DeviceManager(new NotDuplicatedStrategy(new FilePathStrategy())).install(toPaths(a1Files));
  }

  public final Observable<IStatusMonitor> newToken() {
    synchronized(manager) {
      return tokenCycle;
    }
  }
  
  public final IAuthStrategy getAuthStrategy() {
    return this.strategy;
  }

  @Override
  public final void logout() {
    synchronized(manager) {
      doLogout();
    }
  }

  @Override
  public final void close() {
    synchronized(manager) {
      doClose();
    }
  }

  private void doLogout() {
    if (token != null) {
      doDispose(token);
      token = null;
    }
  }
  
  private void doClose() {
    doLogout();
    manager.close();
  }

  
  @Override
  public final void reset() {
    synchronized(manager) {
      close();
      tokenCycle.onComplete();
      tokenCycle = BehaviorSubject.create();
    }
  }
  
  public final void setAuthStrategy(IAuthStrategy strategy) {
    synchronized(manager) {
      if (strategy != null) {
        doSaveStrategy(this.strategy = strategy);
        this.close();      
      }
    }
  }
  
  private T newToken(IDevice device) { 
    synchronized(manager) {
      token = createToken(device.getSlot().getToken(), manager);
      tokenCycle.onNext(token);
      return token;
    }
  }
  
  private void onNewCertificateAvailableCallback(List<IFilePath> a1List, List<IFilePath> a3List) {
    //Heads up with deadlock! This method will run on the event dispatcher thread
    this.a3Libraries = a3List;
    this.a1Files = a1List;
    this.autoForce = true;    
    this.doClose();
    this.manager.install(toPaths(a1List));
  }
  
  private Optional<T> getToken(boolean force, boolean autoSelect) {
    if (Thread.currentThread().isInterrupted()) {
      return Optional.empty();
    }
    synchronized(manager) {
      if (!force && token != null)
        return Optional.of(token);
      
      force |= token == null;
      Optional<ICertificateEntry> selected = showCertificates(force, autoSelect);
      if (!selected.isPresent()) //usuário cancelou a operação!
        return Optional.empty();
  
      DeviceCertificateEntry e = (DeviceCertificateEntry)selected.get();
      this.token = newToken(e.getNative());
      return Optional.of(this.token);
    }
  }

  @Override
  public final Optional<ICertificateEntry> showCertificates(boolean force, boolean autoSelect) {
    IChoice choice;
    synchronized(manager) {     
      do {
        List<IDevice> devices = this.manager.getDevices(this.autoForce || force); 
        choice = CertificateListDialog.display(
          toEntries(devices, getCertificateFilter()), 
          autoSelect, 
          this::onNewCertificateAvailableCallback
        );
      }while(choice == IChoice.NEED_RELOAD);
      this.autoForce = false;
      this.close();
      return choice.get();    
    }
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public T get() {
    synchronized(manager) {
      boolean force = false, autoSelect = true;
      int times = 0;
      do {
        T token = getToken(force, autoSelect).orElseThrow(lambda(LoginCanceledException::new));
        try {
          return (T)token.login();
        } catch (LoginCanceledException e) {
          throw of(e);
        } catch (NoTokenPresentException e) {
          if (!isTrue(NoTokenPresentAlert::display))
            throw of(e);
          this.close();
          force = true;
          autoSelect = false;
        } catch (TokenLockedException e) {
          invokeAndWait(TokenLockedAlert::display);
          throw of(e);
        } catch (ExpiredCredentialException e) {
          invokeAndWait(ExpiredPasswordAlert::display);
          throw of(e);
        } catch (InvalidPinException e) {
          if (TokenType.A3.equals(token.getType()))
            ++times;
          final int t = times;
          if (isTrue(() -> InvalidPinAlert.display(t)))
            continue;
          throw of(e);
        } catch (Signer4JException e) {
          throw of(e);
        }
      }while(true);
    }
  }
  
  protected abstract void doDispose(T token);

  protected abstract void doSaveStrategy(IAuthStrategy strategy);
  
  protected abstract Predicate<ICertificate> getCertificateFilter();

  protected abstract T createToken(IToken token, Object lock);

}
