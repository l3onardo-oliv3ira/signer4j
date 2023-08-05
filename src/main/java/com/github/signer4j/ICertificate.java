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


package com.github.signer4j;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.github.signer4j.cert.ICertificatePF;
import com.github.signer4j.cert.ICertificatePJ;
import com.github.signer4j.cert.IDistinguishedName;
import com.github.signer4j.cert.ISubjectAlternativeNames;
import com.github.signer4j.cert.imp.KeyUsage;

public interface ICertificate extends ISerialItem {
  
  Date getAfterDate();

  Date getBeforeDate();
  
  boolean isExpired();

  IDistinguishedName getCertificateIssuerDN();

  IDistinguishedName getCertificateSubjectDN();

  List<String> getCRLDistributionPoint() throws IOException;

  Optional<String> getEmail();

  Optional<ICertificatePF> getCertificatePF();

  Optional<ICertificatePJ> getCertificatePJ();

  KeyUsage getKeyUsage();

  ISubjectAlternativeNames getSubjectAlternativeNames();

  String getName();

  X509Certificate toX509();

  boolean hasCertificatePF();

  boolean hasCertificatePJ();

  Optional<String> getAlias();

  void setAlias(String alias);
}
