package com.github.signer4j.cert.imp;

import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import com.github.signer4j.ICertificate;
import com.github.signer4j.cert.ICertificateFactory;
import com.github.utils4j.imp.Args;

public enum CertificateFactory implements ICertificateFactory {
  DEFAULT;
  
  @Override
  public ICertificate call(Object input) throws CertificateException {
    Args.requireNonNull(input, "input is null");
    if (input instanceof InputStream)
      return new BrazilianCertificate((InputStream)input);
    if (input instanceof X509Certificate)
      return new BrazilianCertificate((X509Certificate)input);
    throw new CertificateException("Incapaz de criar instância de 'BrazilianCertificate'. Tipo base desconhecido: " + input.getClass());
  }
}
