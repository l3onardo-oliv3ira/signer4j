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


package com.github.signer4j.provider;


import java.security.Provider;

public class Signer4JProvider extends Provider {

  private static final long serialVersionUID = 1L;

  public Signer4JProvider() {
    this("Signer4J", 1.0, "Signer4J Security Provider v1.0");
  }
  
  protected Signer4JProvider(String name, double version, String info) {
    super(name, version, info);
    put("Signature.ASN1MD2withRSA", "com.github.signer4j.provider.ASN1MD2withRSASignature");
    put("Signature.ASN1MD5withRSA", "com.github.signer4j.provider.ASN1MD5withRSASignature");
    put("Signature.ASN1SHA1withRSA", "com.github.signer4j.provider.ASN1SHA1withRSASignature");
    put("Signature.ASN1SHA224withRSA", "com.github.signer4j.provider.ASN1SHA224withRSASignature");
    put("Signature.ASN1SHA256withRSA", "com.github.signer4j.provider.ASN1SHA256withRSASignature");
    put("Signature.ASN1SHA384withRSA", "com.github.signer4j.provider.ASN1SHA384withRSASignature");
    put("Signature.ASN1SHA512withRSA", "com.github.signer4j.provider.ASN1SHA512withRSASignature");

    put("MessageDigest.ASN1MD2", "com.github.signer4j.provider.ASN1MD2MessageDigest");
    put("MessageDigest.ASN1MD5", "com.github.signer4j.provider.ASN1MD5MessageDigest");
    put("MessageDigest.ASN1SHA1", "com.github.signer4j.provider.ASN1SHA1MessageDigest");
    put("MessageDigest.ASN1SHA224", "com.github.signer4j.provider.ASN1SHA224MessageDigest");
    put("MessageDigest.ASN1SHA256", "com.github.signer4j.provider.ASN1SHA256MessageDigest");
    put("MessageDigest.ASN1SHA384", "com.github.signer4j.provider.ASN1SHA384MessageDigest");
    put("MessageDigest.ASN1SHA512", "com.github.signer4j.provider.ASN1SHA512MessageDigest");

    put("Signature.TWOSTEPSwithRSA", "com.github.signer4j.provider.TWOSTEPSwithRSASignature");
  }
}
