package com.github.signer4j;

import com.github.signer4j.gui.JDialogPasswordCallbackHandler;
import com.github.signer4j.imp.LiteralPasswordCallbackHandler;
import com.github.signer4j.imp.exception.KeyStoreAccessException;

public interface IToken extends IGadget{
  
  String getManufacture();
  
  long getMinPinLen();
  
  long getMaxPinLen();
  
  boolean isAuthenticated();
  
  TokenType getType();

  ISlot getSlot();
  
  IKeyStoreAccess getKeyStoreAccess();
  
  ICertificates getCertificates();

  ISignerBuilder signerBuilder();
  
  ISignerBuilder signerBuilder(ICertificateChooserFactory factory);
  
  ICMSSignerBuilder cmsSignerBuilder();
  
  ICMSSignerBuilder cmsSignerBuilder(ICertificateChooserFactory factory);

  void logout();
  
  void login(IPasswordCallbackHandler callback) throws KeyStoreAccessException;

  default IToken login() throws KeyStoreAccessException { 
    return login(p -> {});
  }
  
  default IToken login(IPasswordCollector collector) throws KeyStoreAccessException {
    login(new JDialogPasswordCallbackHandler(this, collector));
    return this;
  }
  
  default IToken login(char[] password) throws KeyStoreAccessException {
    login(new LiteralPasswordCallbackHandler(password));
    return this;
  }
}
