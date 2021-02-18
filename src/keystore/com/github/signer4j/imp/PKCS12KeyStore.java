package com.github.signer4j.imp;

import java.security.KeyStore;
import java.security.PrivateKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.github.signer4j.IDevice;
import com.github.signer4j.imp.exception.KeyStoreAccessException;
import com.github.signer4j.imp.exception.PrivateKeyNotFound;

class PKCS12KeyStore extends AbstractKeyStore {
  
  private byte[] password; 

  private PrivateKey privateKey = null;

  PKCS12KeyStore(IDevice device, KeyStore keystore, char[] password, Runnable dispose) throws PrivateKeyNotFound {
    super(device, keystore, dispose);
    this.password = new String(password).getBytes(Constants.DEFAULT_CHARSET);
  }
  
  @Override
  public final PrivateKey getPrivateKey(String alias) throws KeyStoreAccessException {
    checkIfAvailable();
    if (privateKey == null)
      privateKey = super.getPrivateKey(alias, new String(password, Constants.DEFAULT_CHARSET).toCharArray());
    password = null; 
    return privateKey;
  }
  
  @Override
  public String getProvider() throws KeyStoreAccessException {
    checkIfAvailable();
    return BouncyCastleProvider.PROVIDER_NAME;
  }
  
  @Override
  protected void doClose() throws Exception {
    privateKey = null;
    password = null;
  }
}
