package com.github.signer4j.imp;

import static com.github.utils4j.imp.Args.requireNonEmpty;
import static com.github.utils4j.imp.Args.requireNonNull;
import static com.github.utils4j.imp.Base64.base64Encode;

import java.io.IOException;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.List;

import com.github.signer4j.IPersonalData;
import com.github.signer4j.ISignedData;
import com.github.utils4j.IConstants;

public class SignedData extends CertificateAware implements ISignedData {

  public static ISignedData from(byte[] signedBytes, IPersonalData personalData) {
    return new SignedData(signedBytes, personalData);
  }

  public static ISignedData forTest() {
    return new SignedData("ASSINATURA_MODO_TESTE".getBytes(IConstants.DEFAULT_CHARSET), Choice.CANCEL) {
      @Override
      public String getCertificateChain64() throws CertificateException {
        return base64Encode("coração intrépido infalível".getBytes(IConstants.DEFAULT_CHARSET));
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

  @Override
  public final void writeTo(OutputStream out) throws IOException {
    out.write(signedData);
  }
}
