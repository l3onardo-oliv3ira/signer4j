package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;
import static com.github.signer4j.imp.Args.requireText;
import static com.github.signer4j.imp.Args.requireZeroPositive;
import static com.github.signer4j.imp.KeyStoreInvokeHandler.INVOKER;
import static com.github.signer4j.imp.PKCS11KeyStoreLoaderParams.DRIVER_PATH_PARAM;
import static com.github.signer4j.imp.PKCS11KeyStoreLoaderParams.DRIVER_SLOT_PARAM;
import static java.lang.String.format;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.function.Supplier;

import com.github.signer4j.IDevice;
import com.github.signer4j.IKeyStore;
import com.github.signer4j.IParams;
import com.github.signer4j.IPasswordCallbackHandler;
import com.github.signer4j.imp.exception.KeyStoreAccessException;

import sun.security.pkcs11.SunPKCS11;

@SuppressWarnings("restriction")
class PKCS11KeyStoreLoader extends ExceptionExpert implements IKeyStoreLoader {
  
  private final IPasswordCallbackHandler handler;
  private final IDevice device;
  private final Runnable dispose;
  
  public PKCS11KeyStoreLoader(IPasswordCallbackHandler handler, IDevice device, Runnable dispose) {
    this.handler = requireNonNull(handler, "Unabled to create loader with null handler");
    this.device = requireNonNull(device, "device is null");
    this.dispose = requireNonNull(dispose, "dispose is null");
    
  }
  
  @Override
  public IKeyStore getKeyStore(IParams p) throws KeyStoreAccessException {
    requireNonNull(p, "Params is null");
    return getKeyStore(
      p.orElseThrow(DRIVER_PATH_PARAM, validate()),
      p.orElse(DRIVER_SLOT_PARAM, 0L)
    );
  }

  private IKeyStore getKeyStore(String libraryPath, long slot) throws KeyStoreAccessException {
    requireZeroPositive(slot, "slot must be 0 or positive value");
    libraryPath = requireText(libraryPath, "driver path can't be null").trim().replaceAll("\\\\",  "/");
    int s = libraryPath.lastIndexOf('/');
    s = s <= -1 ? 0 : s + 1;
    String fileName = libraryPath.substring(s, libraryPath.length());
    return getKeyStore(
      providerName(fileName, slot),
      libraryPath,
      slot
    );
  }

  private IKeyStore getKeyStore(String providerName, String libraryPath, long slot) throws KeyStoreAccessException {
    return getKeyStore(
      providerName,
      slot,
      format(//TODO we have to go back here for aditional parameters
        "name = %s\r\nlibrary = %s\r\nslot = %s\r\sattributes = compatibility", 
        providerName,
        libraryPath,
        slot
      ).toString().getBytes()
    );
  }
  
  private IKeyStore getKeyStore(String providerName, long slot, byte[] config) throws KeyStoreAccessException {
    try(InputStream input = new ByteArrayInputStream(config)){
      return getKeyStore(providerName, slot, input); 
    } catch (IOException e) {
      throw new KeyStoreAccessException(e); 
    }
  }
  
  private IKeyStore getKeyStore(String providerName, long slot, InputStream config) throws KeyStoreAccessException {
    final SunPKCS11 provider = Providers.installSunPKCS11Provider(providerName, config);
    return INVOKER.invoke(
      () -> { //try
        provider.login(null, this.handler);
        KeyStore keyStore = KeyStore.getInstance("PKCS11",  provider);
        keyStore.load(null, null); //TODO para todo PKCS11 substitui a senha se já tiver logado?
        return new PKCS11KeyStore(device, keyStore, dispose);
      }, 
      (exception) -> { //catch
        try {
          handleException(exception);
        }finally {
          Providers.logoutAndUninstall(provider);
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
