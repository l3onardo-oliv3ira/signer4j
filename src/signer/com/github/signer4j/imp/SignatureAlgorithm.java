/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package com.github.signer4j.imp;

import static com.github.signer4j.provider.ProviderInstaller.BC;
import static com.github.signer4j.provider.ProviderInstaller.SIGNER4J;

import java.security.Signature;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.signer4j.IAlgorithm;
import com.github.signer4j.IHashAlgorithm;
import com.github.signer4j.ISignatureAlgorithm;
import com.github.signer4j.provider.ANYwithRSASignature;

public enum SignatureAlgorithm implements ISignatureAlgorithm {
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
  SHA512withRSA("SHA512WITHRSA", HashAlgorithm.SHA_512),
  
  //NOT supported two steps
  ASN1MD2withRSA("ASN1MD2withRSA", HashAlgorithm.ASN1MD2),
  ASN1MD5withRSA("ASN1MD5withRSA", HashAlgorithm.ASN1MD5),
  ASN1SHA1withRSA("ASN1SHA1withRSA", HashAlgorithm.ASN1SHA1),
  ASN1SHA224withRSA("ASN1SHA224withRSA", HashAlgorithm.ASN1SHA224),
  ASN1SHA256withRSA("ASN1SHA256withRSA", HashAlgorithm.ASN1SHA256),
  ASN1SHA384withRSA("ASN1SHA384withRSA", HashAlgorithm.ASN1SHA384),
  ASN1SHA512withRSA("ASN1SHA512withRSA", HashAlgorithm.ASN1SHA512);

  static {
    BC.install();
    SIGNER4J.install();
  }
  
  //do not create new array's instances for each call
  private static final SignatureAlgorithm[] VALUES = SignatureAlgorithm.values(); 
  
  @JsonCreator
  public static SignatureAlgorithm fromString(final String key) {
    return from(key).orElse(null);
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
  public final IHashAlgorithm getHashAlgorithm() {
    return hash;
  }
  
  public static SignatureAlgorithm getDefault() {
    return SHA1withRSA;
  }
  
  public static IAlgorithm getOrDefault(String name) {
    return getOfDefault(name, getDefault());
  }
  
  public static SignatureAlgorithm getOfDefault(String name, SignatureAlgorithm defaultIfNot) {
    return from(name).orElse(defaultIfNot);
  }
  
  public static boolean isSupported(String algorithm) {
    return from(algorithm).isPresent();
  }
  
  public static boolean isSupported(IAlgorithm algorithm) {
    return algorithm != null && from(algorithm.getName()).isPresent();
  }
  
  @Override
  public boolean supportsTwoSteps() {
    return hash.supportsTwoSteps() && getKey().endsWith("RSA");
  }
  
  public static Optional<SignatureAlgorithm> from(String name) {
    for(SignatureAlgorithm a: VALUES) {
      if (a.name.equalsIgnoreCase(name))
        return Optional.of(a);
    }
    return Optional.empty();
  }
  
  @Override
  public Signature toSignature() throws Exception {
    if (!supportsTwoSteps()) //ASN1?withRSA
      return Signature.getInstance(getName(), SIGNER4J.defaultName());   //O hash já está pronto e apenas encoda
    //faz o hash usando algoritmo de outro provider e encoda com JCE
    Signature signature = Signature.getInstance("TWOSTEPSwithRSA", SIGNER4J.defaultName());     
    ANYwithRSASignature.HashName hashName = hash::getStandardName;
    signature.setParameter(hashName);
    return signature;
  }
}
