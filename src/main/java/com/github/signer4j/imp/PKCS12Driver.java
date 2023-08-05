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

import static com.github.utils4j.gui.imp.SwingTools.isTrue;
import static com.github.utils4j.imp.Containers.isEmpty;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.github.signer4j.IPasswordCollector;
import com.github.signer4j.ISlot;
import com.github.signer4j.IToken;
import com.github.signer4j.exception.DriverException;
import com.github.signer4j.gui.utils.InvalidPinAlert;
import com.github.signer4j.imp.exception.InvalidPinException;
import com.github.signer4j.imp.exception.LoginCanceledException;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Streams;

class PKCS12Driver extends AbstractDriver {

  private static final PKCS12Driver INSTANCE = new PKCS12Driver();

  public static final PKCS12Driver getInstance() {
    return INSTANCE;
  }
  
  private final List<Path> certPaths = new ArrayList<>();
  
  private PKCS12Driver() {}
  
  @Override
  public final String getId() {
    return PKCS12Driver.class.getSimpleName();
  }
  
  final void uninstall() {
    certPaths.clear();
    unload();
  }
  
  final void close() {
    uninstall();
    Safe.BOX.clear();
  }

  final boolean install(Path ... paths) {
    boolean refreshed = false;
    if (!isEmpty(paths)) {
      for(Path path: paths) {
        if (path == null)
          continue;
        if (!certPaths.stream().anyMatch(p -> Streams.isSame(p, path))) {
          certPaths.add(path);
          refreshed = true;
        }
      }
      if (isLoaded()) {
        reload();
      }
    }
    return refreshed;
  }  
  
  final boolean uninstall(Path... paths) {
    boolean refreshed = false;
    if (!isEmpty(paths)) {
      for(Path path: paths) {
        if (path == null)
          continue;
        if (certPaths.removeIf(p -> Streams.isSame(p, path))) {
          refreshed = true;
        }
      }
      if (isLoaded()) {
        reload();
      }
    }
    return refreshed;
  }
  
  @Override
  protected void loadSlots(List<ISlot> output) throws DriverException {
    certPaths.stream().forEach(path -> {
      try{
        ISlot slot = new PKCS12Slot(path);
        String key = slot.getSerial();
        IToken token = slot.getToken();
        do {
          char[] password = Safe.BOX.get(key);
          try {
            LOGGER.info("Logando no DRIVER");
            if (password != null) {
              LOGGER.info("Logando em modo LITERAL");
              token.login(password);
            } else {
              LOGGER.info("Logando em modo DIALOG");
              AtomicReference<char[]> pass = new AtomicReference<char[]>();
              IPasswordCollector collector = p -> pass.set(p);
              token.login(collector);
              password = pass.get();
            }
            LOGGER.info("Logado com sucesso: " + token.isAuthenticated());
            token.logout();
            LOGGER.info("Deslogado - Leitura de certificado OK");
            Safe.BOX.put(key, password);
            output.add(slot);
            addDevice(slot.toDevice());
            break;
          } catch (LoginCanceledException e) {
            break;
          } catch(InvalidPinException e) {
            Safe.BOX.remove(key);
            if (!isTrue(InvalidPinAlert::display))
              break;
          } catch (Signer4JException e) {
            LOGGER.error("Falha na tentativa de autenticação em PKCS12 Driver: " + path.toString(), e);
            break;
          }
        }while(true);
      }catch(DriverException e) {
        LOGGER.warn("Unabled to loadSlots gracefully", e);
      }      
    });
  }
}
