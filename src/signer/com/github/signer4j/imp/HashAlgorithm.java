package com.github.signer4j.imp;

import java.util.Optional;

import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSSignedDataStreamGenerator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.signer4j.IHashAlgorithm;

public enum HashAlgorithm implements IHashAlgorithm {
  DIGEST_SHA1() {
    @Override
    public String getName() {
      return CMSSignedDataGenerator.DIGEST_SHA1;
    }
  },
  DIGEST_MD5(){
    @Override
    public String getName() {
      return CMSSignedDataStreamGenerator.DIGEST_MD5;
    }
  };

  @JsonCreator
  public static IHashAlgorithm fromString(final String key) {
    return get(key).orElse(null);
  }

  @JsonValue
  public String getKey() {
    return this.name();
  }
  
  //do not create new array's instances for each call
  private static final HashAlgorithm[] VALUES = HashAlgorithm.values(); 

  @Override
  public String getName() {
    return name();
  }
  
  public static IHashAlgorithm getDefault() {
    return DIGEST_SHA1;
  }
  
  public static IHashAlgorithm getOrDefault(String name) {
    return getOfDefault(name, getDefault());
  }
  
  public static IHashAlgorithm getOfDefault(String name, IHashAlgorithm defaultIfNot) {
    return get(name).orElse(defaultIfNot);
  }
  
  public static boolean isSupported(String algorithm) {
    return get(algorithm).isPresent();
  }
  
  public static boolean isSupported(IHashAlgorithm algorithm) {
    return algorithm != null && get(algorithm.getName()).isPresent();
  }
  
  public static Optional<IHashAlgorithm> get(String name) {
    for(IHashAlgorithm a: VALUES) {
      if (a.getName().equalsIgnoreCase(name))
        return Optional.of(a);
    }
    return Optional.empty();
  }
}
