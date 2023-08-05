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

import static sun.security.pkcs11.wrapper.PKCS11Constants.CKA_CLASS;
import static sun.security.pkcs11.wrapper.PKCS11Constants.CKA_TOKEN;
import static sun.security.pkcs11.wrapper.PKCS11Constants.CKO_CERTIFICATE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.IToken;
import com.github.signer4j.cert.ICertificateFactory;
import com.github.signer4j.exception.DriverException;
import com.github.signer4j.exception.DriverFailException;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.ArrayTools;

import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.pkcs11.wrapper.PKCS11Constants;
import sun.security.pkcs11.wrapper.PKCS11Exception;

@SuppressWarnings("restriction")
class PKCS11Certificates extends AbstractCertificates {

  private static final Logger LOGGER = LoggerFactory.getLogger(PKCS11Certificates.class);

  private static final long[] EMPTYLONG = new long[0];

  private static final long FINDOBJECTS_MAX = 10;

  private static final CK_ATTRIBUTE ATTR_TOKEN_TRUE = new CK_ATTRIBUTE(CKA_TOKEN, true);

  private static final CK_ATTRIBUTE ATTR_CLASS_CERT = new CK_ATTRIBUTE(CKA_CLASS, CKO_CERTIFICATE);

  private final transient PKCS11Token token;

  protected PKCS11Certificates(PKCS11Token token, long session, ICertificateFactory factory) throws DriverException {
    super();
    this.token = Args.requireNonNull(token, "token is null");
    this.setup(session, factory);
  }

  final PKCS11 getPk() {
    return token.getPk();
  }

  @Override
  public final IToken getToken() {
    return token;
  }

  private long[] findObjects(long session, CK_ATTRIBUTE[] attrs) throws DriverFailException {
    final PKCS11 pk = getPk();
    long[] objects = EMPTYLONG;

    try {
      pk.C_FindObjectsInit(session, attrs);
      try {
        while (true) {
          long[] h = pk.C_FindObjects(session, FINDOBJECTS_MAX);
          if (h.length == 0) {
            break;
          }
          objects = ArrayTools.concat(objects, h);
        }
      } finally {
        pk.C_FindObjectsFinal(session);
      }
    }catch(PKCS11Exception e) {
      throw new DriverFailException("findObjects fail", e);
    }
    return objects;
  }

  private void setup(long session, ICertificateFactory factory) throws DriverException {
    final PKCS11 pk = getPk();

    CK_ATTRIBUTE[] attrs= new CK_ATTRIBUTE[] {
      ATTR_TOKEN_TRUE,
      ATTR_CLASS_CERT,
    };

    final long[] objects = findObjects(session, attrs);

    try {
      for (long object : objects) {

        attrs = new CK_ATTRIBUTE[]  { new CK_ATTRIBUTE(PKCS11Constants.CKA_VALUE) };
        try {
          pk.C_GetAttributeValue(session, object, attrs);
        } catch (PKCS11Exception e) {
          LOGGER.warn("Unabled to get attribute value from object " + object + ". Token:" + token, e);
          continue;
        }
  
        byte[] certificate = attrs[0].getByteArray();
  
        if (certificate == null) {
          LOGGER.warn("'CKA_VALUE' of certificate " + object + " is null");
          continue;
        }
  
        try (ByteArrayInputStream cert = new ByteArrayInputStream(certificate)) {
          this.certificates.add(factory.create(cert, null));
        } catch (CertificateException | IOException e) {
          LOGGER.warn("Unabled to create certificate instance from byte[]", e);
          continue;
        } 
      }      
    }finally {
      certificates.sort((a, b) -> b.getAfterDate().compareTo(a.getAfterDate()));
    }
  }
}
