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

import static com.github.signer4j.imp.PKCS11KeyStoreLoaderParams.DRIVER_PATH_PARAM;
import static com.github.signer4j.imp.PKCS11KeyStoreLoaderParams.DRIVER_SLOT_PARAM;

import com.github.signer4j.ICertificate;
import com.github.signer4j.IPasswordCallbackHandler;
import com.github.signer4j.IToken;
import com.github.signer4j.TokenType;
import com.github.signer4j.cert.ICertificateFactory;
import com.github.signer4j.exception.DriverException;
import com.github.signer4j.exception.DriverFailException;
import com.github.signer4j.exception.DriverSessionException;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Params;

import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.pkcs11.wrapper.PKCS11Constants;
import sun.security.pkcs11.wrapper.PKCS11Exception;

@SuppressWarnings("restriction")
class PKCS11Token extends AbstractToken<PKCS11Slot> {

  private long minPinLen;
  private long maxPinLen;
  
  private PKCS11Token(PKCS11Slot slot) throws DriverException {
    super(slot, TokenType.A3);
  }
  
  final PKCS11 getPk() {
    return getSlot().getPK();
  }
  
  @Override
  public final long getMinPinLen() {
    return minPinLen;
  }

  @Override
  public final long getMaxPinLen() {
    return maxPinLen;
  }
  
  @Override
  protected final void doLogin(IKeyStore keyStore) throws Signer4JException {    
    for(ICertificate c: certificates) {
      c.setAlias(keyStore.getCertificateAlias(c.toX509())); //this is very important!
    }
  }
  
  @Override
  protected final IKeyStore getKeyStore(IPasswordCallbackHandler callback) throws Signer4JException {
    return new PKCS11KeyStoreLoader(getSlot().toDevice(), callback, getDispose())
      .getKeyStore(
        Params.create()
        .of(DRIVER_PATH_PARAM, getSlot().getLibrary())
        .of(DRIVER_SLOT_PARAM, getSlot().getNumber())
      );
  }
  
  @Override
  public final String toString() {
    return "PKCS11Token [label=" + label + ", model=" + model + ", serial=" + serial + ", manufacture=" + manufacturer
        + ", minPinLen=" + minPinLen + ", maxPinLen=" + maxPinLen + ", certificates=" + certificates + "]";
  }

  @Override
  protected final IToken loadCertificates(ICertificateFactory factory) throws DriverException {
    final PKCS11 pk = getPk();
    long session;
    try {
      session = pk.C_OpenSession(
        getSlot().getNumber(),
        PKCS11Constants.CKF_SERIAL_SESSION,
        null, 
        null
      );
    } catch (PKCS11Exception e) {
      throw new DriverFailException("Unabled to open session on token " + this, e);
    }
    
    try {
      this.certificates = new PKCS11Certificates(
        this, 
        session,
        factory
      );
    } finally {
      try {
        pk.C_CloseSession(session);
      } catch (PKCS11Exception e) {
        throw new DriverSessionException("Unabled to close token session " + this, e);
      }
    }
    return this;
  }

  static class Builder extends AbstractToken.Builder<PKCS11Slot, PKCS11Token> {
    
    private long minPinLen;
    private long maxPinLen;
    
    Builder(PKCS11Slot slot) {
      super(slot);
    }
    
    @Override
    protected void initialize(AbstractToken<PKCS11Slot> tk) {
      super.initialize(tk);
      PKCS11Token token = (PKCS11Token)tk;
      token.minPinLen = this.minPinLen;
      token.maxPinLen = this.maxPinLen;
    }
    
    public final Builder withMinPinLen(long minPinLen) {
      this.minPinLen = minPinLen;
      return this;
    }
    
    public final Builder withMaxPinLen(long maxPinLen) {
      this.maxPinLen = maxPinLen;
      return this;
    }
   
    @Override
    protected AbstractToken<PKCS11Slot> createToken(PKCS11Slot slot) throws DriverException {
      return new PKCS11Token(slot);
    }  
  }
}

