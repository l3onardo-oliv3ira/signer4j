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
  
  private boolean isTrue(int index) {
    return usages != null && index < usages.length && usages[index];
  }

  public KeyUsage(X509Certificate cert) {
    this.usages = cert.getKeyUsage();
  }

  public boolean isDigitalSignature() {
    return isTrue(0);
  }

  public boolean isNonRepudiation() {
    return isTrue(1);
  }

  public boolean isKeyEncipherment() {
    return isTrue(2);
  }

  public boolean isDataEncipherment() {
    return isTrue(3);
  }

  public boolean isKeyAgreement() {
    return isTrue(4);
  }

  public boolean isKeyCertSign() {
    return isTrue(5);
  }

  public boolean isCRLSign() {
    return isTrue(6);
  }

  public boolean isEncipherOnly() {
    return isTrue(7);
  }

  public boolean isDecipherOnly() {
    return isTrue(8);
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
