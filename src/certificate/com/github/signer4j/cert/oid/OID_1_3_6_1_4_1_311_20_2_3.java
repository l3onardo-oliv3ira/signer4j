package com.github.signer4j.cert.oid;

import java.util.Optional;

public final class OID_1_3_6_1_4_1_311_20_2_3 extends OIDBasic {

  public static final String OID = "1.3.6.1.4.1.311.20.2.3";

  protected OID_1_3_6_1_4_1_311_20_2_3(String content) {
    super(OID, content);
  }
  
  public Optional<String> getUPN() {
    return Optional.ofNullable(super.getContent());
  }
}
