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

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;

import com.github.utils4j.INameable;
import com.github.utils4j.imp.NotImplementedException;
import com.github.utils4j.imp.Strings;

public abstract class ANYwithRSASignature extends Signature {

  public static interface HashName extends AlgorithmParameterSpec, INameable {}
  
  private final Signature signature;
  
  protected ANYwithRSASignature(String algorithm) throws Exception {
    super(algorithm);
    this.signature = Signature.getInstance("NONEwithRSA");
  }

  @Override
  protected final void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
    signature.initVerify(publicKey);
  }
  
  @Override
  protected final void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
    signature.initSign(privateKey);
  }

  @Override
  protected final void engineUpdate(byte b) throws SignatureException {
    getDigester().update(b);
  }

  @Override
  protected final void engineUpdate(byte[] b, int off, int len) throws SignatureException {
    getDigester().update(b, off, len);
  }
  
  @Override
  protected final void engineUpdate(ByteBuffer buffer) {
    getDigester().update(buffer);
  }

  @Override
  protected final byte[] engineSign() throws SignatureException {
    signature.update(getDigester().digest());
    return signature.sign();
  }

  @Override
  protected final boolean engineVerify(byte[] sigBytes) throws SignatureException {
    signature.update(getDigester().digest());
    return signature.verify(sigBytes);
  }

  @Override
  protected final void engineSetParameter(String param, Object value) throws InvalidParameterException {
    throw new NotImplementedException("Deprecated method, please use engineSetParameter");
  }

  @Override
  protected final Object engineGetParameter(String param) throws InvalidParameterException {
    throw new NotImplementedException("Deprecated method, please use engineGetParameter");
  }
  
  @Override
  protected final void engineSetParameter(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
    String hashName = Strings.trim(((HashName)params).getName());
    try {
      setupDigester(hashName);
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
      throw new InvalidAlgorithmParameterException("Algorithm '" + hashName + " is not supported", e);
    }
  }
  
  protected abstract MessageDigest getDigester();
  
  protected void setupDigester(String name) throws NoSuchAlgorithmException, NoSuchProviderException {} 
}
