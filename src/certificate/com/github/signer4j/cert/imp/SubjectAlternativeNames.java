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


package com.github.signer4j.cert.imp;

import java.security.cert.X509Certificate;
import java.util.Optional;

import com.github.signer4j.cert.ICertificatePF;
import com.github.signer4j.cert.ICertificatePJ;
import com.github.signer4j.cert.ISubjectAlternativeNames;
import com.github.signer4j.cert.oid.OIDReader;
import com.github.signer4j.cert.oid.OID_1_3_6_1_4_1_311_20_2_3;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_1;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_2;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_3;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_4;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_5;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_6;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_7;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_8;
import com.github.utils4j.imp.Strings;

class SubjectAlternativeNames implements ISubjectAlternativeNames {

  private String email = null;
  private BrazilianCertificatePF certificatePF = null;
  private BrazilianCertificatePJ certificatePJ = null;

  public SubjectAlternativeNames(X509Certificate certificate) {
    setup(certificate);
  }

  @Override
  public final boolean hasCertificatePF() {
    return certificatePF != null;
  }

  @Override
  public final Optional<ICertificatePF> getCertificatePF() {
    return Optional.ofNullable(certificatePF);
  }

  @Override
  public final boolean hasCertificatePJ() {
    return certificatePJ != null;
  }

  @Override
  public final Optional<ICertificatePJ> getCertificatePJ() {
    return Optional.ofNullable(certificatePJ);
  }

  @Override
  public final Optional<String> getEmail() {
    return Strings.optional(email);
  }
  
  private void setup(X509Certificate certificate) {
    OIDReader reader = new OIDReader(certificate);
    if (reader.isCertificatePF()) {
      certificatePF = new BrazilianCertificatePF(
        reader.getOID_2_16_76_1_3_1().orElse(OID_2_16_76_1_3_1.EMPTY), 
        reader.getOID_2_16_76_1_3_5().orElse(OID_2_16_76_1_3_5.EMPTY), 
        reader.getOID_2_16_76_1_3_6().orElse(OID_2_16_76_1_3_6.EMPTY)
      );
    } else if (reader.isCertificatePJ()) {
      certificatePJ = new BrazilianCertificatePJ(
        reader.getOID_2_16_76_1_3_2().orElse(OID_2_16_76_1_3_2.EMPTY), 
        reader.getOID_2_16_76_1_3_3().orElse(OID_2_16_76_1_3_3.EMPTY), 
        reader.getOID_2_16_76_1_3_4().orElse(OID_2_16_76_1_3_4.EMPTY), 
        reader.getOID_2_16_76_1_3_7().orElse(OID_2_16_76_1_3_7.EMPTY),
        reader.getOID_2_16_76_1_3_8().orElse(OID_2_16_76_1_3_8.EMPTY)
      );
    }
    Optional<OID_1_3_6_1_4_1_311_20_2_3> oid = reader.getOID_1_3_6_1_4_1_311_20_2_3();
    if (oid.isPresent()) {
      Optional<String> upn = oid.get().getUPN();
      if (upn.isPresent()) {
        this.email = upn.get();
      }
    } 
    if (!Strings.hasText(this.email)) {
      Optional<String> mail = reader.getEmail();
      if (mail.isPresent()) {
        this.email = mail.get();
      }
    }
  }
}
