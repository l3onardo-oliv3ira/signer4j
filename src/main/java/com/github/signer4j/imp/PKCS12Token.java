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

import static com.github.signer4j.imp.PKCS12KeyStoreLoaderParams.CERTIFICATE_PATH_PARAM;

import com.github.signer4j.IPasswordCallbackHandler;
import com.github.signer4j.IToken;
import com.github.signer4j.TokenType;
import com.github.signer4j.cert.ICertificateFactory;
import com.github.signer4j.exception.DriverException;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Params;

class PKCS12Token extends AbstractToken<PKCS12Slot>{

  private static final int MIN_PASSWORD_LENGTH = 1;
  private static final int MAX_PASSWORD_LENGTH = 31;
  
  private ICertificateFactory factory;
  
  private PKCS12Token(PKCS12Slot slot) throws DriverException {
    super(slot, TokenType.A1);
  }
  
  @Override
  public final long getMinPinLen() {
    return MIN_PASSWORD_LENGTH;
  }

  @Override
  public final long getMaxPinLen() {
    return MAX_PASSWORD_LENGTH;
  }

  @Override
  protected void doLogin(IKeyStore keyStore) throws Signer4JException {
    getSlot().updateDevice(this.certificates = new PKCS12Certificates(this, keyStore, factory));
  }
  
  @Override
  protected IKeyStore getKeyStore(IPasswordCallbackHandler callback) throws Signer4JException {
    return new PKCS12KeyStoreLoader(callback, getSlot().toDevice(), getDispose())
      .getKeyStore(
        Params.create().of(CERTIFICATE_PATH_PARAM, getSlot().getLibrary())
      );
  }
  
  @Override
  public String toString() {
    return "PKCS12Token [label=" + label + ", model=" + model + ", serial=" + serial + ", manufacture=" + manufacturer
        + ", certificates=" + certificates + "]";
  }

  @Override
  protected IToken loadCertificates(ICertificateFactory factory) throws DriverException {
    this.factory = factory;
    this.certificates = Unavailables.getCertificates(this); //só ficará disponível após o login
    return this;
  }
  
  static class Builder extends AbstractToken.Builder<PKCS12Slot, PKCS12Token> {
    
    Builder(PKCS12Slot slot) {
      super(slot);
    }
    
    @Override
    protected AbstractToken<PKCS12Slot> createToken(PKCS12Slot slot) throws DriverException {
      return new PKCS12Token(slot);
    }  
  }
}
