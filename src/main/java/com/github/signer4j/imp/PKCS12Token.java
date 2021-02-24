package com.github.signer4j.imp;

import static com.github.signer4j.imp.PKCS12KeyStoreLoaderParams.CERTIFICATE_PATH_PARAM;

import com.github.signer4j.IKeyStore;
import com.github.signer4j.IPasswordCallbackHandler;
import com.github.signer4j.IToken;
import com.github.signer4j.TokenType;
import com.github.signer4j.cert.ICertificateFactory;
import com.github.signer4j.exception.DriverException;
import com.github.signer4j.imp.exception.KeyStoreAccessException;

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
  protected void doLogin(IKeyStore keyStore) throws KeyStoreAccessException {
    this.certificates = new PKCS12Certificates(this, keyStore, factory);
    getSlot().updateDevice(this.certificates);
  }
  
  @Override
  protected IKeyStore getKeyStore(IPasswordCallbackHandler callback) throws KeyStoreAccessException {
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
