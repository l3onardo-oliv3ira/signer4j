package com.github.signer4j.imp;

import java.security.KeyStore;

import com.github.signer4j.IDevice;
import com.github.signer4j.imp.exception.PrivateKeyNotFound;
import com.github.utils4j.imp.ProviderInstaller;

class PKCS11KeyStore extends AbstractKeyStore {
  
  PKCS11KeyStore(KeyStore keystore, Runnable dispose, IDevice device) throws PrivateKeyNotFound {
    super(keystore, dispose, device);
  }
  
  @Override
  protected void doClose() throws Exception {
    ProviderInstaller.uninstall(keyStore.getProvider());
    super.doClose();
  }
}
