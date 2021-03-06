package com.github.signer4j.imp;

import java.util.Optional;

import org.bouncycastle.cms.CMSSignedDataStreamGenerator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.signer4j.IHashAlgorithm;

public enum HashAlgorithm implements IHashAlgorithm {
  MD2("MD2"),
  MD5("MD5") {
    @Override
    public String getName() {
      return CMSSignedDataStreamGenerator.DIGEST_MD5;
    }
  },
  SHA_1("SHA-1") {
    @Override
    public String getName() {
      return CMSSignedDataStreamGenerator.DIGEST_SHA1;
    }
  },
  SHA_224("SHA-224") {
    @Override
    public String getName() {
      return CMSSignedDataStreamGenerator.DIGEST_SHA224;
    }
  },
  SHA_256("SHA-256") {
    @Override
    public String getName() {
      return CMSSignedDataStreamGenerator.DIGEST_SHA256;
    }
  },
  SHA_384("SHA-384") {
    @Override
    public String getName() {
      return CMSSignedDataStreamGenerator.DIGEST_SHA384;
    }
  },
  SHA_512("SHA-512") {
    @Override
    public String getName() {
      return CMSSignedDataStreamGenerator.DIGEST_SHA512;
    }
  },
  SHA_512_224("SHA-512/224"),
  SHA_512_256("SHA-512/256");
 
  
  static {
    Providers.installBouncyCastleProvider();
  }

  @JsonCreator
  public static IHashAlgorithm fromString(final String key) {
    return get(key).orElse(null);
  }
  
  private final String name;

  HashAlgorithm(String name) {
    this.name = name;
  }
  
  @JsonValue
  public String getKey() {
    return this.name;
  }
  
  @Override
  public String getName() {
    return this.getStandardName();
  }
  
  @Override
  public String getStandardName() {
    return this.name;
  }
  
  //do not create new array's instances for each call
  private static final HashAlgorithm[] VALUES = HashAlgorithm.values(); 

  public static IHashAlgorithm getDefault() {
    return MD5;
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
    for(HashAlgorithm a: VALUES) {
      if (a.name.equalsIgnoreCase(name))
        return Optional.of(a);
    }
    return Optional.empty();
  }
}
