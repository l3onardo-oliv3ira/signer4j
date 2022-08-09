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

import java.util.Optional;

import org.bouncycastle.cms.CMSSignedDataStreamGenerator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.signer4j.IHashAlgorithm;
import com.github.signer4j.provider.ProviderInstaller;

public enum HashAlgorithm implements IHashAlgorithm {
  ASN1MD5("ASN1MD5"),
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
    ProviderInstaller.BC.install();
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
