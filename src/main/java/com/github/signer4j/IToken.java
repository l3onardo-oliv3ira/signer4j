package com.github.signer4j;

import com.github.signer4j.gui.PasswordDialogCallbackHandler;
import com.github.signer4j.imp.LiteralPasswordCallbackHandler;
import com.github.signer4j.imp.exception.KeyStoreAccessException;

public interface IToken extends IGadget {
  
  String getManufacturer();
  
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
  
  IToken login(IPasswordCallbackHandler callback) throws KeyStoreAccessException;

  default IToken login() throws KeyStoreAccessException { 
    return login(p -> {});
  }
  
  default IToken login(IPasswordCollector collector) throws KeyStoreAccessException {
    return login(new PasswordDialogCallbackHandler(this, collector));
  }
  
  default IToken login(char[] password) throws KeyStoreAccessException {
    return login(new LiteralPasswordCallbackHandler(password));
  }
}
