package com.github.signer4j.imp;

import static com.github.signer4j.imp.Base64.base64Encode;

import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import com.github.signer4j.IPersonalData;

abstract class CertificateAware implements IPersonalData {

  private String certificate64Cache;
  
  private String certificateChain64Cache;
  
  @Override
  public final String getCertificate64() throws CertificateException {
    return certificate64Cache != null ? certificate64Cache : 
      (certificate64Cache = base64Encode(new Certificate[] {getCertificate()}));
  }

  @Override
  public String getCertificateChain64() throws CertificateException {
    return certificateChain64Cache != null ? certificateChain64Cache : 
      (certificateChain64Cache = base64Encode(getCertificateChain().toArray(new Certificate[chainSize()])));
  }
}
