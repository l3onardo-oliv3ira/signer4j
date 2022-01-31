package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonEmpty;
import static com.github.signer4j.imp.Args.requireText;

import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.List;

public final class Base64 {

  private static byte[] DEC_BASE64;
  
  private static byte[] ENC_BASE64 = new byte[] { 
    65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 
    85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 
    109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 
    51, 52, 53, 54, 55, 56, 57, 43, 47 
  };
  
  static {
    DEC_BASE64 = new byte[128];
    for (int i = 0; i < ENC_BASE64.length; ++i) {
        DEC_BASE64[ENC_BASE64[i]] = (byte)i;
    }
  }
  
  private Base64() {}
  
  public static String base64Encode(final List<Certificate> chain) throws CertificateException {
    return base64Encode(Certificates.toByteArray(chain));
  }

  public static String base64Encode(final byte[] data) {
    requireNonEmpty(data, "byte array can't be empty");
    final byte[] encodedBuf = new byte[(data.length + 2) / 3 * 4];
    int srcIndex = 0;
    int destIndex = 0;
    while (srcIndex < data.length - 2) {
      encodedBuf[destIndex++] = ENC_BASE64[data[srcIndex] >>> 2 & 0x3F];
      encodedBuf[destIndex++] = ENC_BASE64[(data[srcIndex + 1] >>> 4 & 0xF) | (data[srcIndex] << 4 & 0x3F)];
      encodedBuf[destIndex++] = ENC_BASE64[(data[srcIndex + 2] >>> 6 & 0x3) | (data[srcIndex + 1] << 2 & 0x3F)];
      encodedBuf[destIndex++] = ENC_BASE64[data[srcIndex + 2] & 0x3F];
      srcIndex += 3;
    }
    if (srcIndex < data.length) {
      encodedBuf[destIndex++] = ENC_BASE64[data[srcIndex] >>> 2 & 0x3F];
      if (srcIndex < data.length - 1) {
        encodedBuf[destIndex++] = ENC_BASE64[(data[srcIndex + 1] >>> 4 & 0xF) | (data[srcIndex] << 4 & 0x3F)];
        encodedBuf[destIndex++] = ENC_BASE64[data[srcIndex + 1] << 2 & 0x3F];
      }
      else {
        encodedBuf[destIndex++] = ENC_BASE64[data[srcIndex] << 4 & 0x3F];
      }
    }
    while (destIndex < encodedBuf.length) {
      encodedBuf[destIndex] = 61;
      ++destIndex;
    }
    final String result = new String(encodedBuf);
    return result;
  }

  public static byte[] base64Decode(final String aData) {
    requireText(aData, "unabled to decode empty data");
    byte[] data;
    int tail;
    for (data = aData.getBytes(), tail = data.length; data[tail - 1] == 61; --tail) {}
    final byte[] decodedBuf = new byte[tail - data.length / 4];
    for (int i = 0; i < data.length; ++i) {
      data[i] = DEC_BASE64[data[i]];
    }
    int srcIndex = 0;
    int destIndex;
    for (destIndex = 0; destIndex < decodedBuf.length - 2; destIndex += 3) {
      decodedBuf[destIndex] = (byte)((data[srcIndex] << 2 & 0xFF) | (data[srcIndex + 1] >>> 4 & 0x3));
      decodedBuf[destIndex + 1] = (byte)((data[srcIndex + 1] << 4 & 0xFF) | (data[srcIndex + 2] >>> 2 & 0xF));
      decodedBuf[destIndex + 2] = (byte)((data[srcIndex + 2] << 6 & 0xFF) | (data[srcIndex + 3] & 0x3F));
      srcIndex += 4;
    }
    if (destIndex < decodedBuf.length) {
      decodedBuf[destIndex] = (byte)((data[srcIndex] << 2 & 0xFF) | (data[srcIndex + 1] >>> 4 & 0x3));
    }
    if (++destIndex < decodedBuf.length) {
      decodedBuf[destIndex] = (byte)((data[srcIndex + 1] << 4 & 0xFF) | (data[srcIndex + 2] >>> 2 & 0xF));
    }
    return decodedBuf;
  }
}

