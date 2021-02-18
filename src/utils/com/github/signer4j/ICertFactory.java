package com.github.signer4j;

import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;

public interface ICertFactory {

  X509Certificate generateCertificate(InputStream input) throws CertificateException;

  Iterator<String> getCertPathEncodings();

}
