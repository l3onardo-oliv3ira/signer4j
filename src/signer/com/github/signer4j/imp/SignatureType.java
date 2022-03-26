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
  public static SignatureType fromString(final String key) {
    return get(key).orElse(null);
  }

  @JsonValue
  public String getKey() {
    return this.name();
  }
  
  public static SignatureType getDefault() {
    return ATTACHED;
  }
  
  public static SignatureType getOrDefault(String name) {
    return getOfDefault(name, getDefault());
  }
  
  public static SignatureType getOfDefault(String name, SignatureType defaultIfNot) {
    return get(name).orElse(defaultIfNot);
  }
  
  public static boolean isSupported(String algorithm) {
    return get(algorithm).isPresent();
  }
  
  public static boolean isSupported(ISignatureType algorithm) {
    return algorithm != null && get(algorithm.getName()).isPresent();
  }
  
  public static Optional<SignatureType> get(String name) {
    for(SignatureType a: VALUES) {
      if (a.getName().equalsIgnoreCase(name))
        return Optional.of(a);
    }
    return Optional.empty();
  }
}

