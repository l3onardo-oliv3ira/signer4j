package com.github.signer4j.imp;

import static com.github.signer4j.imp.PKCS11KeyStoreLoaderParams.DRIVER_PATH_PARAM;
import static com.github.signer4j.imp.PKCS11KeyStoreLoaderParams.DRIVER_SLOT_PARAM;

import com.github.signer4j.IPasswordCallbackHandler;
import com.github.signer4j.IToken;
import com.github.signer4j.TokenType;
import com.github.signer4j.cert.ICertificateFactory;
import com.github.signer4j.exception.DriverException;
import com.github.signer4j.exception.DriverFailException;
import com.github.signer4j.exception.DriverSessionException;
import com.github.signer4j.imp.exception.Signer4JException;

import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
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
  protected IKeyStore getKeyStore(IPasswordCallbackHandler callback) throws Signer4JException {
    return new PKCS11KeyStoreLoader(callback, getDispose(), getSlot().toDevice())
      .getKeyStore(
        Params.create()
        .of(DRIVER_PATH_PARAM, getSlot().getLibrary())
        .of(DRIVER_SLOT_PARAM, getSlot().getNumber())
      );
  }
  
  @Override
  public String toString() {
    return "PKCS11Token [label=" + label + ", model=" + model + ", serial=" + serial + ", manufacture=" + manufacturer
        + ", minPinLen=" + minPinLen + ", maxPinLen=" + maxPinLen + ", certificates=" + certificates + "]";
  }

  @Override
  protected IToken loadCertificates(ICertificateFactory factory) throws DriverException {
    final PKCS11 pk = getPk();
    long session;
    try {
      session = pk.C_OpenSession(
        slot.getNumber(),
        PKCS11Constants.CKF_LOGIN_REQUIRED, 
        null, 
        null
      );
    } catch (PKCS11Exception e) {
      throw new DriverFailException("Unabled to open session on token " + this, e);
    }
    
    try {
      try {
        pk.C_FindObjectsInit(session, new CK_ATTRIBUTE[] { 
            new CK_ATTRIBUTE(PKCS11Constants.CKA_TOKEN, true) 
          }
        );
      } catch (PKCS11Exception e) {
        throw new DriverFailException("Unabled to init objects from session token " + this, e);
      }
      
      try {
        this.certificates = new PKCS11Certificates(
          this, 
          session,
          factory
        );
      } finally {
        try {
          pk.C_FindObjectsFinal(session);
        } catch (PKCS11Exception e) {
          throw new DriverSessionException("Unabled to finalize (findObjects) session token " + this, e);
        }
      }     
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
    
    public Builder withMinPinLen(long minPinLen) {
      this.minPinLen = minPinLen;
      return this;
    }
    
    public Builder withMaxPinLen(long maxPinLen) {
      this.maxPinLen = maxPinLen;
      return this;
    }
   
    @Override
    protected AbstractToken<PKCS11Slot> createToken(PKCS11Slot slot) throws DriverException {
      return new PKCS11Token(slot);
    }  
  }
}

