package com.github.signer4j.imp;

import com.github.signer4j.ICMSSignerBuilder;
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
  public IToken login(IPasswordCallbackHandler callback) throws KeyStoreAccessException {
    token.login(callback);
    return this;
  }
  
  @Override
  public IToken login() throws KeyStoreAccessException {
    token.login();
    return this;
  }
  
  @Override
  public IToken login(IPasswordCollector collector) throws KeyStoreAccessException {
    token.login(collector);
    return this;
  }

  @Override
  public IToken login(char[] password) throws KeyStoreAccessException {
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
}
