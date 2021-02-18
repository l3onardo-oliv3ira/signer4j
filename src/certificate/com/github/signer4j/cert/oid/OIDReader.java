package com.github.signer4j.cert.oid;

import static java.util.Optional.ofNullable;

import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OIDReader {

  static final Integer HEADER = 0;
  static final Integer CONTENT = 1;

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
    Collection<List<?>> alternativeNames = null;
    try {
      alternativeNames = certificate.getSubjectAlternativeNames();
    } catch (CertificateParsingException e1) {
      e1.printStackTrace();
    }
    if (alternativeNames == null) {
      return;
    }
    for (List<?> nameItem : alternativeNames) {
      if (nameItem.size() != 2) {
        System.out.println("Subject alternative list must have at least 2 entries (keystore bug?)");
        continue;
      }

      Object header = nameItem.get(HEADER.intValue());
      Object content = nameItem.get(CONTENT.intValue());

      if (!(header instanceof Number)) {
        System.out.println("Type is not a number (keystore bug?)");
        continue;
      }

      Integer type = ((Number)header).intValue();

      if (CONTENT.equals(type)) {
        email = content.toString();
      } else if (HEADER.equals(type)) {
        OIDBasic oid;
        try {
          oid = OIDFactory.create((byte[])content);
          oidPool.put(oid.getOid(), oid);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
