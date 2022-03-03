package com.github.signer4j.imp;

import static com.github.utils4j.gui.imp.SwingTools.isTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.IPasswordCollector;
import com.github.signer4j.ISlot;
import com.github.signer4j.IToken;
import com.github.signer4j.exception.DriverException;
import com.github.signer4j.gui.utils.InvalidPinAlert;
import com.github.signer4j.imp.exception.InvalidPinException;
import com.github.signer4j.imp.exception.LoginCanceledException;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Containers;
import com.github.utils4j.imp.Streams;

class PKCS12Driver extends AbstractDriver {

  private static final Logger LOGGER = LoggerFactory.getLogger(PKCS12Driver.class);
  
  private static final PKCS12Driver INSTANCE = new PKCS12Driver();

  public static final PKCS12Driver getInstance() {
    return INSTANCE;
  }
  
  private final List<Path> certPaths = new ArrayList<>();
  
  private final Map<String, char[]> passwords = new HashMap<>();

  private PKCS12Driver() {}
  
  @Override
  public final String getId() {
    return PKCS12Driver.class.getSimpleName();
  }
  
  final boolean install(Path ... paths) {
    boolean refreshed = false;
    if (!Containers.isEmpty(paths)) {
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
  
  final void uninstall() {
    passwords.clear();
    certPaths.clear();
    if (isLoaded()) {
      unload();
    }
  }
  
  final boolean uninstall(Path... paths) {
    boolean refreshed = false;
    if (!Containers.isEmpty(paths)) {
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
    for(Path path: certPaths) {
      try{
        ISlot slot = new PKCS12Slot(path);
        String key = slot.getSerial();
        IToken token = slot.getToken();
        do {
          char[] password = passwords.get(key);
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
            passwords.put(key, password);
            output.add(slot);
            addDevice(slot.toDevice());
            break;
          } catch (LoginCanceledException e) {
            break;
          } catch(InvalidPinException e) {
            passwords.remove(key);
            if (!isTrue(InvalidPinAlert::display))
              break;
          } catch (Signer4JException e) {
            LOGGER.error("Falha na tentativa de autenticação em PKCS12 Driver: " + path.toString(), e);
            break;
          }
        }while(true);
      }catch(DriverException e) {
        handleException(e);
      }
    }
  }
}
