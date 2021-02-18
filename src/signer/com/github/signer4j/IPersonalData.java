package com.github.signer4j;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.List;

public interface IPersonalData {
  
  PrivateKey getPrivateKey();
  
  Certificate getCertificate();
  
  List<Certificate> getCertificateChain();
  
  int chainSize();
}
