package com.github.signer4j;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;

public interface ISignedData extends IPersonalData {

  byte[] getSignature();

  void writeTo(OutputStream out) throws IOException;
  
  String getSignature64();
  
  String getCertificate64() throws CertificateException;
  
  String getCertificateChain64() throws CertificateException;
}
