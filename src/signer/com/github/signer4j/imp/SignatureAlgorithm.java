package com.github.signer4j.imp;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.signer4j.IAlgorithm;
import com.github.signer4j.ISignatureAlgorithm;

public enum SignatureAlgorithm implements ISignatureAlgorithm {
  MD5withRSA("MD5withRSA"),
  SHA1withRSA("SHA1withRSA"), 
  SHA256withRSA("SHA256withRSA"), 
  ASN1MD5withRSA("ASN1MD5withRSA");
  
  //do not create new array's instances for each call
  private static final SignatureAlgorithm[] VALUES = SignatureAlgorithm.values(); 
  
  @JsonCreator
  public static IAlgorithm fromString(final String key) {
    return get(key).orElse(null);
  }

  private String name;
  
  SignatureAlgorithm(String name) {
    this.name = name;
  }  
  
  @JsonValue
  public String getKey() {
    return name;
  }

  @Override
  public final String getName() {
    return name;
  }
  
  public static ISignatureAlgorithm getDefault() {
    return SHA256withRSA;
  }
  
  public static IAlgorithm getOrDefault(String name) {
    return getOfDefault(name, getDefault());
  }
  
  public static ISignatureAlgorithm getOfDefault(String name, ISignatureAlgorithm defaultIfNot) {
    return get(name).orElse(defaultIfNot);
  }
  
  public static boolean isSupported(String algorithm) {
    return get(algorithm).isPresent();
  }
  
  public static boolean isSupported(IAlgorithm algorithm) {
    return algorithm != null && get(algorithm.getName()).isPresent();
  }
  
  public static Optional<ISignatureAlgorithm> get(String name) {
    for(SignatureAlgorithm a: VALUES) {
      if (a.name.equalsIgnoreCase(name))
        return Optional.of(a);
    }
    return Optional.empty();
  }
}
