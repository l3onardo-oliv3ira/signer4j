package com.github.signer4j.imp;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.signer4j.IAlgorithm;
import com.github.signer4j.ISignatureAlgorithm;

public enum SignatureAlgorithm implements ISignatureAlgorithm {
  ASN1MD5withRSA("ASN1MD5withRSA", HashAlgorithm.ASN1MD5),
  MD2withRSA("MD2WITHRSA", HashAlgorithm.MD2),
  MD5withRSA("MD5WITHRSA", HashAlgorithm.MD5),
  SHA1withDSA("SHA1withDSA", HashAlgorithm.SHA_1),
  SHA1withECDSA("SHA1withECDSA", HashAlgorithm.SHA_1),
  SHA1withPLAIN_ECDSA("SHA1WITHPLAIN-ECDSA", HashAlgorithm.SHA_1),
  SHA1withRSA("SHA1WITHRSA", HashAlgorithm.SHA_1),
  SHA224withCVC_ECDSA("SHA224WITHCVC-ECDSA", HashAlgorithm.SHA_224),
  SHA224withDSA("SHA224WITHDSA", HashAlgorithm.SHA_224),
  SHA224withECDSA("SHA224WITHECDSA", HashAlgorithm.SHA_224),
  SHA224withPLAIN_ECDSA("SHA224WITHPLAIN-ECDSA", HashAlgorithm.SHA_224),
  SHA224withRSA("SHA224WITHRSA", HashAlgorithm.SHA_224),
  SHA256withCVC_ECDSA("SHA256WITHCVC-ECDSA", HashAlgorithm.SHA_256),
  SHA256withDSA("SHA256WITHDSA", HashAlgorithm.SHA_256),
  SHA256withECDSA("SHA256WITHECDSA", HashAlgorithm.SHA_256),
  SHA256withPLAIN_ECDSA("SHA256WITHPLAIN-ECDSA", HashAlgorithm.SHA_256),
  SHA256withRSA("SHA256WITHRSA", HashAlgorithm.SHA_256),
  SHA384withCVC_ECDSA("SHA384WITHCVC-ECDSA", HashAlgorithm.SHA_384),
  SHA384withDSA("SHA384WITHDSA", HashAlgorithm.SHA_384),
  SHA384withECDSA("SHA384WITHECDSA", HashAlgorithm.SHA_384),
  SHA384withPLAIN_ECDSA("SHA384WITHPLAIN-ECDSA", HashAlgorithm.SHA_384),
  SHA384withRSA("SHA384WITHRSA", HashAlgorithm.SHA_384),
  SHA512withCVC_ECDSA("SHA512WITHCVC-ECDSA", HashAlgorithm.SHA_512),
  SHA512withDSA("SHA512WITHDSA", HashAlgorithm.SHA_512),
  SHA512withECDSA("SHA512WITHECDSA", HashAlgorithm.SHA_512),
  SHA512withPLAIN_ECDSA("SHA512WITHPLAIN-ECDSA", HashAlgorithm.SHA_512),
  SHA512withRSA("SHA512WITHRSA", HashAlgorithm.SHA_512);
  
  static {
    ProviderInstaller.BC.install();
  }
  
  //do not create new array's instances for each call
  private static final SignatureAlgorithm[] VALUES = SignatureAlgorithm.values(); 
  
  @JsonCreator
  public static IAlgorithm fromString(final String key) {
    return get(key).orElse(null);
  }

  private String name;
  
  private HashAlgorithm hash;
  
  SignatureAlgorithm(String name, HashAlgorithm hash) {
    this.name = name;
    this.hash = hash;
  }  
  
  @JsonValue
  public String getKey() {
    return name();
  }

  @Override
  public final String getName() {
    return name;
  }
  
  @Override
  public final String getHashName() {
    return hash.getStandardName();
  }
  
  public static ISignatureAlgorithm getDefault() {
    return SHA1withRSA;
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
