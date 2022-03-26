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


package com.github.signer4j.cert.imp;

import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import com.github.signer4j.ICertificate;
import com.github.signer4j.cert.ICertificateFactory;
import com.github.utils4j.imp.Args;

public enum CertificateFactory implements ICertificateFactory {
  DEFAULT;
  
  @Override
  public ICertificate call(Object input) throws CertificateException {
    Args.requireNonNull(input, "input is null");
    if (input instanceof InputStream)
      return new BrazilianCertificate((InputStream)input);
    if (input instanceof X509Certificate)
      return new BrazilianCertificate((X509Certificate)input);
    throw new CertificateException("Incapaz de criar instância de 'BrazilianCertificate'. Tipo base desconhecido: " + input.getClass());
  }
}
