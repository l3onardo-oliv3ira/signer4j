package com.github.signer4j.cert.oid;

import static com.github.utils4j.imp.Args.requireNonNull;
import static com.github.utils4j.imp.Args.requireText;
import static java.lang.Math.min;
import static java.util.Optional.ofNullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class OIDBasic {

  private final String id;
  private final String content;
  
  private final Map<IMetadata, String> properties = new HashMap<IMetadata, String>();
  
  protected OIDBasic(String id, String content) {
    this.id = requireText(id, "Unabled to create OID with empty id");
    this.content = requireNonNull(content, "Unabled to create OID with null data");
  }

  public final String getOid() {
    return id;
  }
  
  protected final Optional<String> get(IMetadata field) {
    return ofNullable(properties.get(field));
  }

  protected final String getContent() {
    return content;
  }

  protected void setup() {
  }
  
  protected static interface IMetadata {
    int length();
  }
  
  protected void setup(IMetadata[] fields) {
    int it = 0;
    for(IMetadata f: fields) {
      int length = f.length();
      properties.put(f, getNullIfDirty(content.substring(it, min(it + length, content.length()))));
      it += length;
    }
  }

  protected static String getNullIfDirty(String value) {
    if (value == null)
      return null;
    int length = (value = value.trim()).length();
    int i = 0;
    while (i < length && value.charAt(i) == '0') 
      i++;
    if (i == length)
      return null;
    return value;
  }
}
