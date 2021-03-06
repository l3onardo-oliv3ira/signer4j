package com.github.signer4j.imp;

import java.security.KeyStore;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.github.signer4j.IDevice;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.signer4j.imp.exception.PrivateKeyNotFound;

class MSCAPIKeyStore extends AbstractKeyStore {
  
  MSCAPIKeyStore(KeyStore keystore, Runnable dispose, IDevice device) throws PrivateKeyNotFound {
    super(keystore, dispose, device);
  }
  
  @Override
  public String getProvider() throws Signer4JException {
    checkIfAvailable();
    return BouncyCastleProvider.PROVIDER_NAME;
  }
}
