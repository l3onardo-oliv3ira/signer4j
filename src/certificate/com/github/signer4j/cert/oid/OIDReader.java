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


package com.github.signer4j.cert.oid;

import static java.util.Optional.ofNullable;

import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.utils4j.imp.Args;

public class OIDReader {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(OIDReader.class);

  static final Integer HEADER   = 0;
  static final Integer CONTENT  = 1;
  
  private static final Integer OID_TYPE   = 0;
  private static final Integer EMAIL_TYPE = 1;
  //private static final Integer DNS_TYPE = 2;

  private final Map<String, OIDBasic> oidPool = new HashMap<>();

  private String email = null;

  public OIDReader(X509Certificate certificate) {
    setup(certificate);
  }
  
  @SuppressWarnings("unchecked")
  private final <T extends OIDBasic> Optional<T> get(String oid) {
    return ofNullable((T)oidPool.get(oid));
  }

  public final Optional<String> getEmail() {
    return ofNullable(email);
  }
  
  public final boolean isCertificatePF() {
    return get(OID_2_16_76_1_3_1.OID).isPresent();
  }

  public final boolean isCertificatePJ() {
    return get(OID_2_16_76_1_3_7.OID).isPresent();
  }

  public final Optional<OID_2_16_76_1_3_1> getOID_2_16_76_1_3_1() {    
    return get(OID_2_16_76_1_3_1.OID);
  }

  public final Optional<OID_2_16_76_1_3_5> getOID_2_16_76_1_3_5() {
    return get(OID_2_16_76_1_3_5.OID);
  }

  public final Optional<OID_2_16_76_1_3_6> getOID_2_16_76_1_3_6() {
    return get(OID_2_16_76_1_3_6.OID);
  }

  public final Optional<OID_2_16_76_1_3_2> getOID_2_16_76_1_3_2() {
    return get(OID_2_16_76_1_3_2.OID);
  }

  public final Optional<OID_2_16_76_1_3_3> getOID_2_16_76_1_3_3() {
    return get(OID_2_16_76_1_3_3.OID);
  }

  public final Optional<OID_2_16_76_1_3_4> getOID_2_16_76_1_3_4() {
    return get(OID_2_16_76_1_3_4.OID);
  }

  public final Optional<OID_2_16_76_1_3_7> getOID_2_16_76_1_3_7() {
    return get(OID_2_16_76_1_3_7.OID);
  }

  public final Optional<OID_2_16_76_1_3_8> getOID_2_16_76_1_3_8() {
    return get(OID_2_16_76_1_3_8.OID);
  }
  
  public final Optional<OID_1_3_6_1_4_1_311_20_2_3> getOID_1_3_6_1_4_1_311_20_2_3() {
    return get(OID_1_3_6_1_4_1_311_20_2_3.OID);
  }
  
  public final Optional<OID_2_16_76_1_3_9> getOID_2_16_76_1_3_9() {
    return get(OID_2_16_76_1_3_9.OID);
  }
  
  private void setup(X509Certificate certificate) {
    Args.requireNonNull(certificate, "certificate is null");
    Collection<List<?>> alternativeNames = null;
    try {
      alternativeNames = certificate.getSubjectAlternativeNames();
    } catch (CertificateParsingException e1) {
      LOGGER.warn("Não foi possível ler 'subjectAlternativeNames' do certificado. Provável certificado não pertencente ao ICPBRASIL", e1);
    }
    if (alternativeNames == null) {
      return;
    }
    for (List<?> nameItem : alternativeNames) {
      if (nameItem.size() != 2) {
        LOGGER.warn("Subject alternative list must have at least 2 entries (keystore bug?)");
        continue;
      }

      Object typeHeader = nameItem.get(HEADER.intValue());
      Object content = nameItem.get(CONTENT.intValue());

      if (!(typeHeader instanceof Number)) {
        LOGGER.warn("typeHeader is not a number (keystore bug?)");
        continue;
      }

      Integer type = ((Number)typeHeader).intValue();

      if (EMAIL_TYPE.equals(type)) {
        email = content.toString();
      } else if (OID_TYPE.equals(type)) {
        OIDBasic oid;
        try {
          oid = OIDFactory.create((byte[])content);
          oidPool.put(oid.getOid(), oid);
        } catch (Exception e) {
          LOGGER.warn("Não foi possível criar OID a partir do array de bytes", e);
        }
      }
    }
  }
}
