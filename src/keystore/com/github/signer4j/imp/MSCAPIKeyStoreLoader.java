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

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import com.github.signer4j.IDevice;
import com.github.signer4j.imp.exception.ModuleException;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.IParams;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Params;

class MSCAPIKeyStoreLoader implements IKeyStoreLoader{

  private static final String MSCAPI_TYPE = "Windows-MY";

  private static final String MSCAPI_PROVIDER = "SunMSCAPI";

  private IDevice device;
  
  private Runnable dispose;

  public MSCAPIKeyStoreLoader(IDevice device, Runnable dispose) {
    this.device = Args.requireNonNull(device, "device is null");
    this.dispose = Args.requireNonNull(dispose, "dispose is null");
  }
  
  public IKeyStore getKeyStore() throws Signer4JException {
    return getKeyStore(Params.EMPTY);
  }
  
  @Override
  public IKeyStore getKeyStore(IParams params) throws Signer4JException {
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
    
    return new MSCAPIKeyStore(keystore, dispose, device);
  }
}
