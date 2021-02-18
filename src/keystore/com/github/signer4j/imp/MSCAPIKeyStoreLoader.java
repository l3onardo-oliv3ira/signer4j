package com.github.signer4j.imp;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import com.github.signer4j.IDevice;
import com.github.signer4j.IKeyStoreAccess;
import com.github.signer4j.IParams;
import com.github.signer4j.imp.exception.KeyStoreAccessException;
import com.github.signer4j.imp.exception.ModuleException;

class MSCAPIKeyStoreLoader implements IKeyStoreLoader{

  private static final String MSCAPI_TYPE = "Windows-MY";

  private static final String MSCAPI_PROVIDER = "SunMSCAPI";

  private IDevice device;
  
  private Runnable dispose;

  protected MSCAPIKeyStoreLoader(IDevice device, Runnable dispose) {
    this.device = Args.requireNonNull(device, "device is null");
    this.dispose = Args.requireNonNull(dispose, "dispose is null");
  }
  
  public IKeyStoreAccess getKeyStore() throws KeyStoreAccessException {
    return getKeyStore(Params.EMPTY);
  }

  @Override
  public IKeyStoreAccess getKeyStore(IParams params) throws KeyStoreAccessException {
    KeyStore keystore;
    try {
      keystore = KeyStore.getInstance(MSCAPI_TYPE, MSCAPI_PROVIDER);
    } catch (KeyStoreException | NoSuchProviderException e) {
      throw new ModuleException("Unabled to create MSCAPI KeyStore instance", e);
    }
    
    try {
      keystore.load(null, null);
    } catch (NoSuchAlgorithmException | CertificateException | IOException e) {
      throw new ModuleException("Unabled to load native mscapi keystore", e);
    }

    MscapiFixer.BUG_6483657.fix(keystore);
    
    return new MSCAPIKeyStore(device, keystore, dispose);
  }
}
