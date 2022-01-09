package com.github.signer4j;

import com.github.signer4j.gui.PasswordDialogCallbackHandler;
import com.github.signer4j.imp.LiteralPasswordCallbackHandler;
import com.github.signer4j.imp.exception.Signer4JException;

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

  IPKCS7SignerBuilder pkcs7SignerBuilder();
  
  IPKCS7SignerBuilder pkcs7SignerBuilder(ICertificateChooserFactory factory);
  
  ICertificateChooser createChooser(ICertificateChooserFactory factory);

  default ICertificateChooser createChooser() {
    return createChooser(ICertificateChooserFactory.DEFAULT);
  }

  void logout();
  
  IToken login(IPasswordCallbackHandler callback) throws Signer4JException;

  default IToken login() throws Signer4JException { 
    return login(p -> {});
  }
  
  default IToken login(IPasswordCollector collector) throws Signer4JException {
    return login(new PasswordDialogCallbackHandler(this, collector));
  }
  
  default IToken login(char[] password) throws Signer4JException {
    return login(new LiteralPasswordCallbackHandler(password));
  }
}
