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

import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.List;

import com.github.signer4j.IChoice;
import com.github.signer4j.IKeyStoreAccess;
import com.github.signer4j.ISignatureAlgorithm;
import com.github.signer4j.ISignedData;
import com.github.signer4j.ISigner;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Args;

class Choice extends CertificateAware implements IChoice {
  static final IChoice CANCEL = new Choice();

  static IChoice from(IKeyStoreAccess keyStore, String choosenAlias) throws Signer4JException {
    return Choice.from(
      keyStore.getPrivateKey(choosenAlias),
      keyStore.getCertificate(choosenAlias),
      keyStore.getCertificateChain(choosenAlias),
      keyStore.getProvider()
    );
  }

  private static IChoice from(PrivateKey privateKey, Certificate certificate, List<Certificate> chain, String provider) {
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
  public final ISigner toSigner(ISignatureAlgorithm algorithm) throws Exception {
    Args.requireNonNull(algorithm, "algorithm is null");
    return MSCAPI.defaultName().equals(provider) ?
      new MSCAPISigner(Signature.getInstance(algorithm.getName(), MSCAPI.defaultName())) :
      new DefaultSigner(algorithm.toSignature());
  }  
  
  private class MSCAPISigner extends DefaultSigner {
    
    MSCAPISigner(Signature signature) {
      super(signature);
    }
  
    @Override
    protected ISignedData doSign() {
      return NoTokenPresent.HANDLER.handle(() -> Signer4JInvoker.SIGNER4J.invoke(super::doSign), MSCAPISigner.class);
    }
  }
  
  private class DefaultSigner implements ISigner {
    
    protected final Signature signature;
    
    protected DefaultSigner(Signature signature) {
      this.signature = signature;
    }
  
    @Override
    public final ISignedData sign(byte[] content, int offset, int length) throws Exception {
      signature.initSign(Choice.this.getPrivateKey());
      signature.update(content, offset, length);
      return doSign();
    }
    
    protected ISignedData doSign() throws Exception {
      return SignedData.from(signature.sign(), Choice.this);
    }
  }
}  
