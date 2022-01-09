package com.github.signer4j.imp;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.List;

public class Choice extends CertificateAware implements IChoice {
  public static final IChoice CANCEL = new Choice();
 
  public static IChoice from(PrivateKey privateKey, Certificate certificate, List<Certificate> chain, String provider) {
    return new Choice(false, privateKey, certificate, chain, provider);
  }
  
  private final boolean canceled;
  private final PrivateKey privateKey;
  private final Certificate certificate;
  private final List<Certificate> chain;
  private final String provider;
  
  private Choice() {
    this(true, null, null, Collections.emptyList(), null);
  }
  
  private Choice(boolean canceled, PrivateKey privateKey, Certificate certificate, List<Certificate> chain, String provider) {
    this.canceled = canceled;
    this.privateKey = privateKey;
    this.certificate = certificate;
    this.chain = chain;
    this.provider = provider;
  }

  @Override
  public final boolean isCanceled() {
    return this.canceled;
  }

  @Override
  public final PrivateKey getPrivateKey() {
    return this.privateKey;
  }

  @Override
  public final Certificate getCertificate() {
    return this.certificate;
  }

  @Override
  public final List<Certificate> getCertificateChain() {
    return chain;
  }

  @Override
  public final int chainSize() {
    return chain.size();
  }

  @Override
  public final String getProvider() {
    return this.provider;
  }
}
