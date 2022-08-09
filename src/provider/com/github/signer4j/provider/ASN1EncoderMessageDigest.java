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

import java.io.IOException;

import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;

import com.github.utils4j.imp.Args;

public abstract class ASN1EncoderMessageDigest extends EncoderMessageDigest {

  private final ASN1ObjectIdentifier hashId;
  
  protected ASN1EncoderMessageDigest(String name, ASN1ObjectIdentifier hashId) {
    super(name);
    this.hashId = Args.requireNonNull(hashId, "hashId is null");
  }

  //Encode To PKCS1 By BounceCastle
  protected byte[] encode(byte[] digest) {
    try {
      return new DigestInfo(new AlgorithmIdentifier(hashId, DERNull.INSTANCE), digest).getEncoded(ASN1Encoding.DER);
    } catch (IOException e) {
      throw new RuntimeException("Unabled to encode bytes to ASN1Encoding.DER", e);
    }
  }
}
