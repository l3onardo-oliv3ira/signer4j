package com.github.signer4j.cert.imp;

import java.security.cert.X509Certificate;


/***
 * JAVADOC: 
 * the KeyUsage extension of this certificate, represented as an array of booleans. The order of KeyUsage 
 * values in the array is the same as in the above ASN.1 definition. The array will contain a value for 
 * each KeyUsage defined above. If the KeyUsage list encoded in the certificate is longer than the 
 * above list, it will not be truncated. Returns null if this certificate does not contain a 
 * KeyUsage extension.
 */
public final class KeyUsage {

  private static final String[] USAGES = { 
    "digitalSignature", 
    "nonRepudiation", 
    "keyEncipherment", 
    "dataEncipherment", 
    "keyAgreement", 
    "keyCertSign", 
    "cRLSign", 
    "encipherOnly", 
    "decipherOnly" 
  };

  private final boolean[] usages;

  public KeyUsage(X509Certificate cert) {
    this.usages = cert.getKeyUsage();
  }

  public boolean isDigitalSignature() {
    return usages[0];
  }

  public boolean isNonRepudiation() {
    return usages[1];
  }

  public boolean isKeyEncipherment() {
    return usages[2];
  }

  public boolean isDataEncipherment() {
    return usages[3];
  }

  public boolean isKeyAgreement() {
    return usages[4];
  }

  public boolean isKeyCertSign() {
    return usages[5];
  }

  public boolean isCRLSign() {
    return usages[6];
  }

  public boolean isEncipherOnly() {
    return usages[7];
  }

  public boolean isDecipherOnly() {
    return usages[8];
  }

  @Override
  public String toString() {
    String out = "";
    if (usages != null) {
      for (int i = 0; i < usages.length; i++) {
        if (usages[i]) {
          if (out.length() > 0) {
            out += ", ";
          }
          out += USAGES[i];
        }
      }
    }
    return out;
  }
}
