/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package com.github.signer4j.imp;

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
import com.github.utils4j.imp.Args;

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
    this.signedData = Args.requireNonEmpty(signedBytes, "empty signed bytes");
    this.personalData = Args.requireNonNull(personalData, "personalData is null");
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
