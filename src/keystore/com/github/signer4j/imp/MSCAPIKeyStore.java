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

import static com.github.signer4j.provider.ProviderInstaller.MSCAPI;

import java.security.KeyStore;
import java.security.PrivateKey;

import com.github.signer4j.IDevice;
import com.github.signer4j.imp.exception.PrivateKeyNotFound;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.signer4j.provider.ProviderInstaller;

class MSCAPIKeyStore extends AbstractKeyStore {
  
  MSCAPIKeyStore(KeyStore keystore, IDevice device, Runnable dispose) throws PrivateKeyNotFound {
    super(keystore, device, dispose);
  }
  
  @Override
  public String getProvider() throws Signer4JException {
    checkIfAvailable();
    return MSCAPI.defaultName();
  }
  
  @Override
  protected void doClose() throws Exception {
    ProviderInstaller.uninstall(keyStore.getProvider());
    super.doClose();
  }
  
  @Override
  protected void onInitKey(PrivateKey key) throws Exception {
//    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", getProvider());
//    cipher.init(Cipher.ENCRYPT_MODE, key);
//    cipher.doFinal("unlock".getBytes());
  }
}
