package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;

import java.security.cert.CertificateException;
import java.util.Enumeration;

import com.github.signer4j.ICertificate;
import com.github.signer4j.IToken;
import com.github.signer4j.cert.ICertificateFactory;
import com.github.signer4j.imp.exception.KeyStoreAccessException;

class KeyStoreCertificates extends AbstractCertificates {

  private final transient IToken token;
  
  KeyStoreCertificates(IToken token, IKeyStore keyStore, ICertificateFactory factory) throws KeyStoreAccessException {
    super(factory);
    this.token = requireNonNull(token, "token is null");
    this.setup(keyStore, factory);
  }
  
  @Override
  public final IToken getToken() {
    return token;
  }
  
  private void setup(IKeyStore keyStore, ICertificateFactory factory) throws KeyStoreAccessException {
    requireNonNull(keyStore, "keyStore is null");
    Enumeration<String> aliases = keyStore.getAliases();
    while(aliases.hasMoreElements()) {
      String aliasName = aliases.nextElement();
      ICertificate certificate;
      try {
        certificate = factory.call(keyStore.getCertificate(aliasName));
      } catch (CertificateException e) {
        reset();
        throw new KeyStoreAccessException(e);
      } catch (KeyStoreAccessException e) {
        reset();
        throw e;
      }
      super.certificates.add(certificate);
    }
  }

  private void reset() {
    super.certificates.clear();
  }
}
