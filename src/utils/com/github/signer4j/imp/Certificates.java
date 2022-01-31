package com.github.signer4j.imp;

import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

public class Certificates {
  private static final String X509_CERTIFICATE_TYPE = "X.509";

  private static final String CERTIFICATION_CHAIN_ENCODING = "PkiPath";
  
  private static CertificateFactory FACTORY;
  
  private static CertificateFactory getFactory() throws CertificateException {
    return FACTORY == null ? FACTORY = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE) : FACTORY;
  }
  
  private Certificates() {}
  
  public static X509Certificate create(InputStream is) throws CertificateException {
    Args.requireNonNull(is, "inputstream is null");
    return (X509Certificate)getFactory().generateCertificate(is);
  }
  
  public static byte[] toByteArray(final List<Certificate> chain) throws CertificateException {
    Args.requireNonEmpty(chain, "chain is empty");
    return getFactory().generateCertPath(chain).getEncoded(CERTIFICATION_CHAIN_ENCODING);
  }
}
