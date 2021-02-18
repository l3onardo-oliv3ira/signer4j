package com.github.signer4j.imp;

import java.security.Key;

import javax.crypto.Cipher;

public final class Ciphers {
  
  private Ciphers() {}
  
  public static final String ALGORITHM_RSA = "RSA/ECB/PKCS1Padding";

  public static byte[] encryptWithRsa(final byte[] message, final Key key) throws Exception {
    return encrypt(message, key, ALGORITHM_RSA);
  }

  public static byte[] decryptWithRsa(final byte[] message, final Key key) throws Exception {
    return decrypt(message, key, ALGORITHM_RSA);
  }

  public static byte[] encrypt(final byte[] message, final Key key, final String algorithm) throws Exception {
    final Cipher cipher = Cipher.getInstance(algorithm);
    cipher.init(1, key);
    return cipher.doFinal(message);
  }

  public static byte[] decrypt(final byte[] message, final Key key, final String algorithm) throws Exception {
    final Cipher cipher = Cipher.getInstance(algorithm);
    cipher.init(2, key);
    return cipher.doFinal(message);
  }
}
