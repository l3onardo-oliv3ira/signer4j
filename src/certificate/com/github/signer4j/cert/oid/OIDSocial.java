package com.github.signer4j.cert.oid;

import java.util.Optional;

abstract class OIDSocial extends OIDBasic {

  private static enum Fields implements IMetadata {
    CEI(12);

    private final int length;
    
    Fields(int length) {
      this.length = length;
    }
    
    @Override
    public int length() {
      return length;
    }
  }
  
  protected OIDSocial(String oid, String content) {
    super(oid, content);
  }
   
  @Override
  protected void setup() {
    super.setup(Fields.values());
  }

  public Optional<String> getCEI() {
    return get(Fields.CEI);
  }
}
