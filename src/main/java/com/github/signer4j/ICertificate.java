package com.github.signer4j;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.github.signer4j.cert.ICertificatePF;
import com.github.signer4j.cert.ICertificatePJ;
import com.github.signer4j.cert.IDistinguishedName;
import com.github.signer4j.cert.ISubjectAlternativeNames;
import com.github.signer4j.cert.imp.KeyUsage;

public interface ICertificate extends ISerialItem {
  
  Date getAfterDate();

  Date getBeforeDate();

  IDistinguishedName getCertificateIssuerDN();

  IDistinguishedName getCertificateSubjectDN();

  List<String> getCRLDistributionPoint() throws IOException;

  Optional<String> getEmail();

  Optional<ICertificatePF> getCertificatePF();

  Optional<ICertificatePJ> getCertificatePJ();

  KeyUsage getKeyUsage();

  ISubjectAlternativeNames getSubjectAlternativeNames();

  String getName();

  X509Certificate toX509();

  boolean hasCertificatePF();

  boolean hasCertificatePJ();
}
