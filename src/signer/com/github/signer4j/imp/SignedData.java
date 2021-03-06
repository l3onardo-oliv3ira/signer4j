package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonEmpty;
import static com.github.signer4j.imp.Args.requireNonNull;
import static com.github.signer4j.imp.Base64.base64Encode;

import java.io.IOException;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.List;

import com.github.signer4j.IPersonalData;
import com.github.signer4j.ISignedData;

public class SignedData implements ISignedData {

  public static ISignedData from(byte[] signedBytes, IPersonalData personalData) {
    return new SignedData(signedBytes, personalData);
  }

  public static ISignedData forTest() {
    return new SignedData("test_mode_content".getBytes(Constants.DEFAULT_CHARSET), Choice.CANCEL) {
      @Override
      public String getCertificateChain64() throws CertificateException {
        return base64Encode("test_mode_certificate_chain".getBytes(Constants.DEFAULT_CHARSET));
      }
    };
  }
  
  private final byte[] signedData;
  
  private final IPersonalData personalData;
  
  private SignedData(byte[] signedBytes, IPersonalData personalData) {
    this.signedData = requireNonEmpty(signedBytes, "empty signed bytes");
    this.personalData = requireNonNull(personalData, "personalData is null");
  }
  
  @Override
  public final PrivateKey getPrivateKey() {
    return personalData.getPrivateKey();
  }

  @Override
  public final Certificate getCertificate() {
    return personalData.getCertificate();
  }

  @Override
  public final List<Certificate> getCertificateChain() {
    return personalData.getCertificateChain();
  }

  @Override
  public final int chainSize() {
    return personalData.chainSize();
  }

  @Override
  public final byte[] getSignature() {
    return signedData;
  }
  
  private String signature64Cache;
  @Override
  public final String getSignature64() {
    return signature64Cache != null ? signature64Cache : 
      (signature64Cache = base64Encode(signedData));
  }

  private String certificate64Cache;
  @Override
  public final String getCertificate64() throws CertificateException {
    return certificate64Cache != null ? certificate64Cache : 
      (certificate64Cache = base64Encode(new Certificate[] {getCertificate()}));
  }

  private String certificateChain64Cache;
  @Override
  public String getCertificateChain64() throws CertificateException {
    return certificateChain64Cache != null ? certificateChain64Cache : 
      (certificateChain64Cache = base64Encode(getCertificateChain().toArray(new Certificate[chainSize()])));
  }

  @Override
  public final void writeTo(OutputStream out) throws IOException {
    out.write(signedData);
  }
}
