package com.github.signer4j.imp;

import java.security.KeyStore;

import com.github.signer4j.IDevice;
import com.github.signer4j.imp.exception.PrivateKeyNotFound;

class PKCS11KeyStore extends AbstractKeyStore {
  
  PKCS11KeyStore(IDevice device, KeyStore keystore, Runnable dispose) throws PrivateKeyNotFound {
    super(device, keystore, dispose);
  }
  
  @Override
  protected void doClose() throws Exception {
    Providers.logoutAndUninstall(keyStore.getProvider());
    super.doClose();
  }
}
