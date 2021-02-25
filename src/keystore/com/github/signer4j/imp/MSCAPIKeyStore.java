package com.github.signer4j.imp;

import java.security.KeyStore;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.github.signer4j.IDevice;
import com.github.signer4j.imp.exception.KeyStoreAccessException;
import com.github.signer4j.imp.exception.PrivateKeyNotFound;

class MSCAPIKeyStore extends AbstractKeyStore {
  
  MSCAPIKeyStore(IDevice device, KeyStore keystore, Runnable dispose) throws PrivateKeyNotFound {
    super(device, keystore, dispose);
  }
  
  @Override
  public String getProvider() throws KeyStoreAccessException {
    checkIfAvailable();
    return BouncyCastleProvider.PROVIDER_NAME;
  }
}
