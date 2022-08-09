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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.security.rsa.RSASignature;
import sun.security.x509.AlgorithmId;

@SuppressWarnings("restriction")
public class RSAEncoderMessageDigest extends EncoderMessageDigest {

  private final MessageDigest digester;

  private final AlgorithmId hashId;
  
  public RSAEncoderMessageDigest(String hashId) throws NoSuchAlgorithmException {
    super("RSAEncoder");
    this.digester = MessageDigest.getInstance(hashId);
    this.hashId = AlgorithmId.get(hashId);
  }
  
  @Override
  protected byte[] doDigest(byte[] input) {
    return digester.digest(input);
  }

  //Encode To PKCS1 By SunJCE
  @Override
  protected byte[] encode(byte[] digest) {
    try {
      return RSASignature.encodeSignature(hashId.getOID(), digest);
    } catch (IOException e) {
      throw new RuntimeException("Unabled to encode bytes to sun.security.util.DerValue", e);
    }
  }
}
