package com.github.signer4j.imp;

import com.github.signer4j.ICMSSignerBuilder;
import com.github.signer4j.ICertificateChooserFactory;
import com.github.signer4j.ICertificates;
import com.github.signer4j.IKeyStoreAccess;
import com.github.signer4j.IPasswordCallbackHandler;
import com.github.signer4j.ISignerBuilder;
import com.github.signer4j.ISlot;
import com.github.signer4j.IToken;
import com.github.signer4j.TokenType;
import com.github.signer4j.imp.exception.KeyStoreAccessException;

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
  public String getSerialNumber() {
    return token.getSerialNumber();
  }

  @Override
  public String getManufacture() {
    return token.getManufacture();
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
  public void login(IPasswordCallbackHandler callback) throws KeyStoreAccessException {
    token.login(callback);
  }

  @Override
  public void logout() {
    token.logout();
  }

  @Override
  public TokenType getType() {
    return token.getType();
  }
}
