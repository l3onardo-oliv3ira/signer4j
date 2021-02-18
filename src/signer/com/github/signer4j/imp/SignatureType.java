package com.github.signer4j.imp;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.signer4j.ISignatureType;

public enum SignatureType implements ISignatureType {
  ATTACHED, 
  DETACHED;

  private static final SignatureType[] VALUES = SignatureType.values(); 
  
  @Override
  public String getName() {
    return name();
  }
  
  @JsonCreator
  public static ISignatureType fromString(final String key) {
    return get(key).orElse(null);
  }

  @JsonValue
  public String getKey() {
    return this.name();
  }
  
  public static ISignatureType getDefault() {
    return ATTACHED;
  }
  
  public static ISignatureType getOrDefault(String name) {
    return getOfDefault(name, getDefault());
  }
  
  public static ISignatureType getOfDefault(String name, ISignatureType defaultIfNot) {
    return get(name).orElse(defaultIfNot);
  }
  
  public static boolean isSupported(String algorithm) {
    return get(algorithm).isPresent();
  }
  
  public static boolean isSupported(ISignatureType algorithm) {
    return algorithm != null && get(algorithm.getName()).isPresent();
  }
  
  public static Optional<ISignatureType> get(String name) {
    for(ISignatureType a: VALUES) {
      if (a.getName().equalsIgnoreCase(name))
        return Optional.of(a);
    }
    return Optional.empty();
  }
}

