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

import java.security.cert.CertificateException;
import java.util.Enumeration;

import com.github.signer4j.ICertificate;
import com.github.signer4j.IToken;
import com.github.signer4j.cert.ICertificateFactory;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Args;

class KeyStoreCertificates extends AbstractCertificates {

  private final transient IToken token;
  
  KeyStoreCertificates(IToken token, IKeyStore keyStore, ICertificateFactory factory) throws Signer4JException {
    super();
    this.token = Args.requireNonNull(token, "token is null");
    this.setup(keyStore, factory);
  }
  
  @Override
  public final IToken getToken() {
    return token;
  }
  
  private void setup(IKeyStore keyStore, ICertificateFactory factory) throws Signer4JException {
    Args.requireNonNull(keyStore, "keyStore is null");
    Enumeration<String> aliases = keyStore.getAliases();
    while(aliases.hasMoreElements()) {
      String aliasName = aliases.nextElement();
      ICertificate certificate;
      try {
        certificate = factory.create(keyStore.getCertificate(aliasName), aliasName);
      } catch (CertificateException e) {
        reset();
        throw new Signer4JException(e);
      } catch (Signer4JException e) {
        reset();
        throw e;
      }
      super.certificates.add(certificate);
    }

    super.certificates.sort((a, b) -> b.getAfterDate().compareTo(a.getAfterDate()));
  }

  private void reset() {
    super.certificates.clear();
  }
}
