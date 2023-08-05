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

import java.util.Optional;

import com.github.signer4j.ICMSSignerBuilder;
import com.github.signer4j.ICertificate;
import com.github.signer4j.ICertificateChooser;
import com.github.signer4j.ICertificateChooserFactory;
import com.github.signer4j.ICertificates;
import com.github.signer4j.IKeyStoreAccess;
import com.github.signer4j.IPKCS7SignerBuilder;
import com.github.signer4j.IPasswordCallbackHandler;
import com.github.signer4j.IPasswordCollector;
import com.github.signer4j.ISignerBuilder;
import com.github.signer4j.ISlot;
import com.github.signer4j.IToken;
import com.github.signer4j.TokenType;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Args;

import io.reactivex.Observable;

public class TokenWrapper implements IToken {

  protected final IToken token;
  
  protected TokenWrapper(IToken token) {
    this.token = Args.requireNonNull(token, "token is null");
  }
  
  @Override
  public String getLabel() {
    return token.getLabel();
  }

  @Override
  public String getModel() {
    return token.getModel();
  }

  @Override
  public String getSerial() {
    return token.getSerial();
  }

  @Override
  public String getManufacturer() {
    return token.getManufacturer();
  }

  @Override
  public long getMinPinLen() {
    return token.getMinPinLen();
  }

  @Override
  public long getMaxPinLen() {
    return token.getMaxPinLen();
  }

  @Override
  public boolean isAuthenticated() {
    return token.isAuthenticated();
  }

  @Override
  public Observable<Boolean> getStatus() {
    return token.getStatus();
  }
  
  @Override
  public ISlot getSlot() {
    return token.getSlot();
  }
  
  @Override
  public IKeyStoreAccess getKeyStoreAccess() {
    return token.getKeyStoreAccess();
  }

  @Override
  public ICertificates getCertificates() {
    return token.getCertificates();
  }

  @Override
  public ISignerBuilder signerBuilder() {
    return token.signerBuilder();
  }

  @Override
  public ISignerBuilder signerBuilder(ICertificateChooserFactory factory) {
    return token.signerBuilder(factory);
  }

  @Override
  public ICMSSignerBuilder cmsSignerBuilder() {
    return token.cmsSignerBuilder();
  }

  @Override
  public ICMSSignerBuilder cmsSignerBuilder(ICertificateChooserFactory factory) {
    return token.cmsSignerBuilder(factory);
  }
  
  @Override
  public IPKCS7SignerBuilder pkcs7SignerBuilder() {
    return token.pkcs7SignerBuilder();
  }

  @Override
  public IPKCS7SignerBuilder pkcs7SignerBuilder(ICertificateChooserFactory factory) {
    return token.pkcs7SignerBuilder(factory);
  }

  @Override
  public IToken login(IPasswordCallbackHandler callback) throws Signer4JException {
    token.login(callback);
    return this;
  }
  
  @Override
  public IToken login() throws Signer4JException {
    token.login();
    return this;
  }
  
  @Override
  public IToken login(IPasswordCollector collector) throws Signer4JException {
    token.login(collector);
    return this;
  }

  @Override
  public IToken login(char[] password) throws Signer4JException {
    token.login(password);
    return this;
  }

  @Override
  public void logout() {
    token.logout();
  }

  @Override
  public TokenType getType() {
    return token.getType();
  }
  
  @Override
  public String getCategory() {
    return token.getCategory();
  }

  @Override
  public ICertificateChooser createChooser(ICertificateChooserFactory factory) {
    return token.createChooser(factory);
  }
  
  @Override
  public ICertificateChooser createChooser() {
    return token.createChooser();
  }

  @Override
  public void setDefaultCertificate(ICertificate certificate) {
    token.setDefaultCertificate(certificate);
  }
  
  @Override
  public Optional<ICertificate> getDefaultCertificate() {
    return token.getDefaultCertificate();
  }
}
