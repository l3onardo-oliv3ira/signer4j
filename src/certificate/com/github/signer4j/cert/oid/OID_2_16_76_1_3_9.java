package com.github.signer4j.cert.oid;

import java.util.Optional;

public final class OID_2_16_76_1_3_9 extends OIDBasic {

  public static final String OID = "2.16.76.1.3.9";

  private static enum Fields implements IMetadata {
    RIC(11);

    private final int length;
    
    Fields(int length) {
      this.length = length;
    }
    
    @Override
    public int length() {
      return length;
    }
  }

  protected OID_2_16_76_1_3_9(String content) {
    super(OID, content);
  }

  @Override
  public void setup() {
    super.setup(Fields.values());
  }

  public Optional<String> getRegistroDeIdentidadeCivil() {
    return get(Fields.RIC);
  }
}