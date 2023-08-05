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

import static com.github.progress4j.imp.ProgressFactories.withSimple;
import static com.github.signer4j.IFilePath.toPaths;
import static com.github.signer4j.imp.DeviceCertificateEntry.from;
import static com.github.utils4j.gui.imp.SwingTools.isTrue;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.github.signer4j.IAuthStrategy;
import com.github.signer4j.ICertificate;
import com.github.signer4j.ICertificateListUI.ICertificateEntry;
import com.github.signer4j.ICertificateListUI.IChoice;
import com.github.signer4j.IDevice;
import com.github.signer4j.IDeviceManager;
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
import com.github.signer4j.imp.exception.InterruptedSigner4JRuntimeException;
import com.github.signer4j.imp.exception.InvalidPinException;
import com.github.signer4j.imp.exception.LoginCanceledException;
import com.github.signer4j.imp.exception.NoTokenPresentException;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.signer4j.imp.exception.TokenLockedException;
import com.github.utils4j.imp.Args;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public abstract class TokenAccessor<T extends IToken> implements ITokenAccess<T> {
  
  private volatile T token;

  private Disposable ticketTokenLogout;

  protected final IDeviceManager manager;

  private volatile IAuthStrategy strategy;

  private volatile boolean autoForce = true;

  private BehaviorSubject<IStatusMonitor> tokenCycle = BehaviorSubject.create();
  
  protected TokenAccessor(AuthStrategy strategy, IDeviceManager deviceManager) {
    this.strategy = Args.requireNonNull(strategy, "strategy is null");
    this.manager = Args.requireNonNull(deviceManager, "deviceManager is null");
  }

  @Override
  public final Observable<IStatusMonitor> newToken() {
    synchronized(manager) {
      return tokenCycle;
    }
  }
  
  @Override
  public final Repository getRepository() {
    synchronized(manager) {
      return manager.getRepository();
    }
  }
  
  protected final Object lockInstance() {
    return manager;
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

  private void doClose() {
    try {
      doLogout();
    }finally {
      manager.close();
    }
  }

  private void doLogout() {
    if (token != null) {
      doDispose(token);
      token = null;
    }
    if (ticketTokenLogout != null) {
      ticketTokenLogout.dispose();
      ticketTokenLogout = null;
    }
  }
  
  @Override
  public final void reset() {
    synchronized(manager) {
      close();
      tokenCycle.onComplete();
      tokenCycle = BehaviorSubject.create();
    }
  }
  
  @Override
  public final void setAuthStrategy(IAuthStrategy strategy) {
    if (strategy != null) {
      synchronized(manager) {
        doSaveStrategy(this.strategy = strategy);
        close();      
      }
    }
  }
  
  private T newToken(IDevice device, ICertificate defaultCertificate) { 
    T token = createToken(device.getSlot().getToken(), manager);
    token.setDefaultCertificate(defaultCertificate);
    ticketTokenLogout = token.getStatus().subscribe(this::checkStatus);
    tokenCycle.onNext(token);
    return token;
  }
  
  private void checkStatus(Boolean online) {
    if (!online) {
      logout();
    }
  }
  
  private void onCertificateAvailable(List<IFilePath> a1List, List<IFilePath> a3List) {
    //Heads up with deadlock! This method will run on the event dispatcher thread
    try {
      doCertificateAvailable(a1List, a3List);
    }finally {
      this.autoForce = true;    
      this.doClose();
      this.manager.install(toPaths(a1List));
    }
  }
  
  protected void doCertificateAvailable(List<IFilePath> a1List, List<IFilePath> a3List) {
    ;//explicit nothing implementation
  }
  
  private T getToken(boolean force) throws SwitchRepositoryException {
        
    if (!force && token != null)
      return token;

    Optional<ICertificateEntry> selected = showCertificates(force, true, true);
    
    if (!selected.isPresent()) { //usuário cancelou a operação!
      Signer4jContext.discard();
    }

    DeviceCertificateEntry e = (DeviceCertificateEntry)selected.get();

    token = newToken(e.getNative(), e.getCertificate());

    return this.token;
  }
  
  @Override
  public final Optional<ICertificateEntry> showCertificates(boolean force, boolean autoSelect, boolean repoWaiting) throws SwitchRepositoryException {

    final Supplier<List<ICertificateEntry>> supplier = () -> from(manager.getDevices(autoForce || force), getCertificateFilter());

    synchronized(manager) {
      IChoice choice;
      try {

        do {          
          
          boolean showProgress = autoForce || force || !manager.isLoaded();

          List<ICertificateEntry> entries = showProgress ? callWithProgress(supplier) : supplier.get();
          
          choice = CertificateListDialog.display(entries, repoWaiting, autoSelect, this::onCertificateAvailable, manager.getRepository());
          
        }while(this.autoForce = choice == IChoice.NEED_RELOAD);
        
      } catch (SwitchRepositoryException e) {
        autoForce = true;        
        Signer4jContext.discardQuietly(e::setAvailable);        
        throw e;
      }
      
      return choice.get();    
    }
  }
  
  private List<ICertificateEntry> callWithProgress(Supplier<List<ICertificateEntry>> supplier) {
    //Faço o close em currentThread e o load em nova thread (health checking). Isto é importante ser feito aqui para evitar deadlock em synchronized(manager)
    close();

    return withSimple(p -> {
      try {
        p.begin("Lendo certificados do dispositivo...");

        AtomicBoolean displayDelayed = new AtomicBoolean(true);

        //I only display if there is a delay greater than 650ms (avoids screen flickering if the certificate load is too fast)
        p.displayAsync(650, displayDelayed::get); 
        
        //45 seconds for the driver to load, otherwise, I understand that the driver is not responding!
        List<ICertificateEntry> entries = DriverHealth.CHECKER.check(supplier, Duration.ofSeconds(45));
        
        //At this point, if the delay has not been reached, then I no longer need to display the progress because the entries have already been loaded.
        displayDelayed.set(false);

        p.end();
        
        return entries;
      } catch (InterruptedException e) {
        throw new InterruptedSigner4JRuntimeException(e);
      } 
    });
  }    

  @Override
  @SuppressWarnings("unchecked")
  public final T call() throws SwitchRepositoryException {
    
    synchronized(manager) {
      
      Signer4jContext.checkDiscarded(true);
      
      boolean force = false;
      int times = 0;
      do {
        T token = getToken(force);
        try {
        
          return (T)token.login();
        
        } catch (LoginCanceledException e) {
          Signer4jContext.discard(e);

        } catch (NoTokenPresentException e) {
          if (!isTrue(NoTokenPresentAlert::display)) {
            Signer4jContext.discard(e);
          }
          close();
          force = true;

        } catch (TokenLockedException e) {
          TokenLockedAlert.showAndWait();
          Signer4jContext.discard(e);

        } catch (ExpiredCredentialException e) {
          ExpiredPasswordAlert.showAndWait();
          Signer4jContext.discard(e);

        } catch (InvalidPinException e) {
          if (TokenType.A3.equals(token.getType()))
            ++times;
          final int t = times;
          if (isTrue(() -> InvalidPinAlert.display(t)))
            continue;
          Signer4jContext.discard(e);

        } catch (Signer4JException e) {
          Signer4jContext.discard(e);
        }
        
      }while(true);
    }
  }
  
  protected abstract void doDispose(T token);
  
  protected abstract T createToken(IToken token, Object lock);

  protected abstract void doSaveStrategy(IAuthStrategy strategy);
  
  protected abstract Predicate<ICertificate> getCertificateFilter();

}
