package com.github.signer4j;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.List;

public interface IPersonalData {
  
  PrivateKey getPrivateKey();
  
  Certificate getCertificate();
  
  List<Certificate> getCertificateChain();
  
  String getCertificate64() throws CertificateException;

  String getCertificateChain64() throws CertificateException;
  
  int chainSize();
}
