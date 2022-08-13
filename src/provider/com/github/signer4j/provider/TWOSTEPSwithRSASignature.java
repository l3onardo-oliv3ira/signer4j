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

import java.security.InvalidAlgorithmParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import com.github.utils4j.imp.States;

/**
 * Esta abordagem de assinatura em duas etapas explícitas (hash + rsa) permite fazer 
 * uso de algoritmos de Hash's que não fazem parte da coleção de algoritimos oferecidos
 * por um provider específico PKCS11. É comum alguns tokens de certificados digitais (GD Burti por exemplo)
 * não disponibilizarem implementações de hash's antigas como MD5 (mas ainda virem com o RSA) 
 * e que ainda são utilizadas por aplicações, então, separando o cálculo em duas etapas, deixamos o 
 * uso da chave privada dos tokens A3 para lidar APENAS com a cifra RSA das mensagens, 
 * ou seja, o calculo do hash é feito FORA do driver do token e o RSA é feito DENTRO do token, 
 * maximizando a compatibilidade com tokens de diversos fabricantes. Este é o núcleo do problemas que 
 * faziam as pessoa me "encherem o saco" por defeitos de alguns assinadores do judiciário que não 
 * sabem lidar com este problema e ainda pioravam com uso da api de reflexão java como esforço para  
 * "compatibilizar-se" com a MSCAPI. FINALMENTE essa bagaça foi resolvida e terei paz! ¯\_(ツ)_/¯
 */

public class TWOSTEPSwithRSASignature extends ANYwithRSASignature {

  private MessageDigest digester;
  
  public TWOSTEPSwithRSASignature() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException{
    super("TWOSTEPSwithRSA");
  }

  private void checkDigestAvailable() {
    States.requireNonNull(digester != null, "digest is not defined by engineSetParameter");
  }
  
  @Override
  protected MessageDigest getDigester() {
    checkDigestAvailable();
    return digester;
  }
  
  @Override
  protected void setupDigester(String hashName) throws NoSuchAlgorithmException, NoSuchProviderException {
    digester = new RSAEncoderMessageDigest(hashName);
  }
}
