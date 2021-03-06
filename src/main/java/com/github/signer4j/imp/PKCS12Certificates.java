package com.github.signer4j.imp;

import com.github.signer4j.cert.ICertificateFactory;
import com.github.signer4j.imp.exception.Signer4JException;

class PKCS12Certificates extends KeyStoreCertificates {
  PKCS12Certificates(PKCS12Token token, IKeyStore keyStore, ICertificateFactory factory) throws Signer4JException {
    super(token, keyStore, factory);
  }
}
