package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;
import static com.github.signer4j.imp.Args.requireText;
import static com.github.signer4j.imp.Args.requireZeroPositive;
import static com.github.signer4j.imp.PKCS11KeyStoreLoaderParams.DRIVER_PATH_PARAM;
import static com.github.signer4j.imp.PKCS11KeyStoreLoaderParams.DRIVER_SLOT_PARAM;
import static com.github.signer4j.imp.ProviderInstaller.SUNPKCS11;
import static com.github.signer4j.imp.ProviderInstaller.uninstall;
import static com.github.signer4j.imp.Signer4JInvoker.INVOKER;
import static java.lang.String.format;

import java.security.AuthProvider;
import java.security.KeyStore;
import java.util.function.Supplier;

import com.github.signer4j.IDevice;
import com.github.signer4j.IParams;
import com.github.signer4j.IPasswordCallbackHandler;
import com.github.signer4j.imp.exception.Signer4JException;

class PKCS11KeyStoreLoader extends ExceptionExpert implements IKeyStoreLoader {
  
  private final IPasswordCallbackHandler handler;
  private final IDevice device;
  private final Runnable dispose;
  
  PKCS11KeyStoreLoader() {
    this(PasswordCallbackHandler.CONSOLE);
  }

  PKCS11KeyStoreLoader(IPasswordCallbackHandler handler) {
    this(handler, () -> {});
  }

  PKCS11KeyStoreLoader(IPasswordCallbackHandler handler, Runnable dispose) {
    this(handler, dispose, null);
  }
  
  PKCS11KeyStoreLoader(IPasswordCallbackHandler handler, Runnable dispose, IDevice device) {
    this.handler = requireNonNull(handler, "Unabled to create loader with null handler");
    this.dispose = requireNonNull(dispose, "dispose is null");
    this.device = device;
  }
  
  @Override
  public IKeyStore getKeyStore(IParams p) throws Signer4JException {
    requireNonNull(p, "Params is null");
    return getKeyStore(
      p.orElseThrow(DRIVER_PATH_PARAM, validate()),
      p.orElse(DRIVER_SLOT_PARAM, 0L)
    );
  }

  private IKeyStore getKeyStore(String libraryPath, long slot) throws Signer4JException {
    requireZeroPositive(slot, "slot must be 0 or positive value");
    libraryPath = requireText(libraryPath, "driver path can't be null").trim().replace('\\',  '/');
    int s = libraryPath.lastIndexOf('/');
    s = s <= -1 ? 0 : s + 1;
    String fileName = libraryPath.substring(s, libraryPath.length());
    return getKeyStore(
      providerName(fileName, slot),
      libraryPath,
      slot
    );
  }

  private IKeyStore getKeyStore(String providerName, String libraryPath, long slot) throws Signer4JException {
    return getKeyStore(
      providerName,
      format(//TODO we have to go back here for aditional parameters
        "name = %s\r\nlibrary = %s\r\nslot = %s\r\sattributes = compatibility", 
        providerName,
        libraryPath,
        slot
      ).toString()
    );
  }
  
  private IKeyStore getKeyStore(String providerName, String configString) throws Signer4JException {
    final AuthProvider provider = (AuthProvider)SUNPKCS11.install(providerName, configString);
    return INVOKER.invoke(
      () -> { //try
        provider.login(null, this.handler);
        KeyStore keyStore = KeyStore.getInstance("PKCS11",  provider);
        keyStore.load(null, null);
        return new PKCS11KeyStore(keyStore, dispose, device);
      }, 
      (exception) -> { //catch
        try {
          handleException(exception);
        }finally {
          uninstall(provider);
        }
      });
  }

  private static Supplier<? extends IllegalArgumentException> validate() {
    return () -> new IllegalArgumentException(DRIVER_PATH_PARAM + " is undefined");
  }
  
  private static String providerName(String fileName, long slot) {
    return format("%s-slot%s", fileName, slot);
  }
}
