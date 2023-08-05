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

import static com.github.signer4j.imp.Signer4JInvoker.SIGNER4J;
import static com.github.signer4j.provider.ProviderInstaller.uninstall;

import java.security.KeyStore;
import java.security.Provider;

import com.github.signer4j.IDevice;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.signer4j.provider.ProviderInstaller;
import com.github.utils4j.IParams;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Params;

class MSCAPIKeyStoreLoader implements IKeyStoreLoader{

  private static final String MSCAPI_TYPE = "Windows-MY";
  
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
    return SIGNER4J.invoke(
      () -> {
        final Provider provider = ProviderInstaller.MSCAPI.install();

        provider.put("Signature.ASN1MD2withRSA", "sun.security.mscapi.LITERALwithRSASignature$MD2withRSA");
        provider.put("Signature.ASN1MD5withRSA", "sun.security.mscapi.LITERALwithRSASignature$MD5withRSA");
        provider.put("Signature.ASN1SHA1withRSA", "sun.security.mscapi.LITERALwithRSASignature$SHA1withRSA");
        provider.put("Signature.ASN1SHA256withRSA", "sun.security.mscapi.LITERALwithRSASignature$SHA256withRSA");
        provider.put("Signature.ASN1SHA384withRSA", "sun.security.mscapi.LITERALwithRSASignature$SHA384withRSA");
        provider.put("Signature.ASN1SHA512withRSA", "sun.security.mscapi.LITERALwithRSASignature$SHA512withRSA");
        
        try {
          KeyStore keyStore = KeyStore.getInstance(MSCAPI_TYPE, provider);
          keyStore.load(null, null);
          return new MSCAPIKeyStore(keyStore, device, dispose);
        } catch(Throwable e) {
          uninstall(provider);
          throw e;
        }
      }
    );
  }
}
