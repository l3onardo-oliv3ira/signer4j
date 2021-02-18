package com.github.signer4j.cert.oid;

import java.util.Optional;

public final class OID_2_16_76_1_3_3 extends OIDBasic {

  public static final String OID = "2.16.76.1.3.3";

  protected OID_2_16_76_1_3_3(String content) {
    super(OID, content);
  }
  
	public Optional<String> getCNPJ() {
		return Optional.of(super.getContent());
	}
}
