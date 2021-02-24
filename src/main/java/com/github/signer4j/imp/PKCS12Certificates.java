package com.github.signer4j.imp;

import com.github.signer4j.IKeyStore;
import com.github.signer4j.cert.ICertificateFactory;
import com.github.signer4j.imp.exception.KeyStoreAccessException;

class PKCS12Certificates extends KeyStoreCertificates {
  PKCS12Certificates(PKCS12Token token, IKeyStore keyStore, ICertificateFactory factory) throws KeyStoreAccessException {
    super(token, keyStore, factory);
  }
}
