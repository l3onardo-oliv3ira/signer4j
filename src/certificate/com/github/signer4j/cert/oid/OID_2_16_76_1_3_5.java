package com.github.signer4j.cert.oid;

import java.util.Optional;

public final class OID_2_16_76_1_3_5 extends OIDBasic {

  public static final String OID = "2.16.76.1.3.5";

  private static enum Fields implements IMetadata {
    ELECTORAL_DOCUMENT(12), 
    ZONE(3), 
    SECTION(4), 
    CITY_UF(22);

    private final int length;
    
    Fields(int length) {
      this.length = length;
    }
    
    @Override
    public int length() {
      return length;
    }
  }
  
  protected OID_2_16_76_1_3_5(String content) {
    super(OID, content);
  }

  @Override
  protected void setup() {
    super.setup(Fields.values());
  }

  public Optional<String> getElectoralDocument() {
    return get(Fields.ELECTORAL_DOCUMENT);
  }

  public Optional<String> getZone() {
    return get(Fields.ZONE);
  }

  public Optional<String> getSection() {
    return get(Fields.SECTION);
  }

  public Optional<String> getCityUF() {
    Optional<String> value = get(Fields.CITY_UF);
    if (value.isPresent()) {
      String s = value.get();
      int len = s.length();
      if (len > 2) {
        return Optional.of(s.substring(0, len - 2));
      }
    }
    return value;
  }

  public Optional<String> getUFDocument() {
    Optional<String> value = get(Fields.CITY_UF);
    if (value.isPresent()) {
      String s = value.get();
      int len = s.length();
      if (len > 1) {
        return Optional.of(s.substring(len - 2, len));
      }
    }
    return value;
  }
}
