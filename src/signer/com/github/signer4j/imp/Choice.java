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

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.List;

import com.github.signer4j.IChoice;

public class Choice extends CertificateAware implements IChoice {
  public static final IChoice CANCEL = new Choice();
 
  public static IChoice from(PrivateKey privateKey, Certificate certificate, List<Certificate> chain, String provider) {
    return new Choice(false, privateKey, certificate, chain, provider);
  }
  
  private final boolean canceled;
  private final PrivateKey privateKey;
  private final Certificate certificate;
  private final List<Certificate> chain;
  private final String provider;
  
  private Choice() {
    this(true, null, null, Collections.emptyList(), null);
  }
  
  private Choice(boolean canceled, PrivateKey privateKey, Certificate certificate, List<Certificate> chain, String provider) {
    this.canceled = canceled;
    this.privateKey = privateKey;
    this.certificate = certificate;
    this.chain = chain;
    this.provider = provider;
  }

  @Override
  public final boolean isCanceled() {
    return this.canceled;
  }

  @Override
  public final PrivateKey getPrivateKey() {
    return this.privateKey;
  }

  @Override
  public final Certificate getCertificate() {
    return this.certificate;
  }

  @Override
  public final List<Certificate> getCertificateChain() {
    return chain;
  }

  @Override
  public final int chainSize() {
    return chain.size();
  }

  @Override
  public final String getProvider() {
    return this.provider;
  }  
}
