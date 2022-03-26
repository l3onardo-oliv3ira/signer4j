/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


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
