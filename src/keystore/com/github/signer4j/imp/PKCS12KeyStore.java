package com.github.signer4j.imp;

import java.security.KeyStore;
import java.security.PrivateKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.github.signer4j.IDevice;
import com.github.signer4j.imp.exception.PrivateKeyNotFound;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.IConstants;

class PKCS12KeyStore extends AbstractKeyStore {
  
  private byte[] password; 

  private PrivateKey privateKey = null;

  PKCS12KeyStore(KeyStore keystore, Runnable dispose, IDevice device, char[] password) throws PrivateKeyNotFound {
    super(keystore, dispose, device);
    this.password = new String(password).getBytes(IConstants.DEFAULT_CHARSET);
  }
  
  @Override
  public final PrivateKey getPrivateKey(String alias) throws Signer4JException {
    checkIfAvailable();
    if (privateKey == null)
      privateKey = super.getPrivateKey(alias, new String(password, IConstants.DEFAULT_CHARSET).toCharArray());
    password = null; 
    return privateKey;
  }
  
  @Override
  public String getProvider() throws Signer4JException {
    checkIfAvailable();
    return BouncyCastleProvider.PROVIDER_NAME;
  }
  
  @Override
  protected void doClose() throws Exception {
    privateKey = null;
    password = null;
    super.doClose();
  }
}
