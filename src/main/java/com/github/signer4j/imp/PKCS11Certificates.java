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

import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.pkcs11.wrapper.PKCS11Constants;
import sun.security.pkcs11.wrapper.PKCS11Exception;

@SuppressWarnings("restriction")
class PKCS11Certificates extends AbstractCertificates {

  private static final Logger LOGGER = LoggerFactory.getLogger(PKCS11Certificates.class);
  
  private final transient PKCS11Token token;
  
  protected PKCS11Certificates(PKCS11Token token, long session, ICertificateFactory factory) throws DriverException {
    super(factory);
    this.token = Args.requireNonNull(token, "token is null");
    this.setup(session);
  }
  
  final PKCS11 getPk() {
    return token.getPk();
  }

  @Override
  public final IToken getToken() {
    return token;
  }
  
  private void setup(long session) throws DriverException {
    final PKCS11 pk = getPk();
    final long maxObjectCount = 999L;
    long[] objects = new long[0];
    
    try {
      objects = pk.C_FindObjects(session, maxObjectCount);
    } catch (PKCS11Exception e) {
      throw new DriverFailException("Unabled to find object certificates from token " + token, e);
    }
    
    try {
      for(final long object: objects) {
        CK_ATTRIBUTE[] attributes = {new CK_ATTRIBUTE(PKCS11Constants.CKA_LABEL)};
        
        try {
          pk.C_GetAttributeValue(session, object, attributes);
        } catch (PKCS11Exception e) {
          LOGGER.debug("Unabled to get attribute value from session:  token " + token, e);
          continue;
        }
        
        CK_ATTRIBUTE aliasAttrib = attributes[0];
        
        if (!hasAlias(aliasAttrib)) {
          LOGGER.debug("'CKA_LABEL' attribute not found for object: '" + object + "'");
          continue;
        }
        
        attributes = new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(PKCS11Constants.CKA_VALUE) };
        try {
          pk.C_GetAttributeValue(session, object, attributes);
        } catch (PKCS11Exception e3) {
          LOGGER.debug("'CKA_VALUE' not found for object '" + object + "'");
          continue;
        }
        
        CK_ATTRIBUTE certificateAttrib = attributes[0];
        
        if (!hasCertificate(certificateAttrib)) {
          LOGGER.debug("'CKA_VALUE' not found for object '" + object + "'");
          continue;
        }

        Object value = certificateAttrib.pValue;
        if (!(value instanceof byte[])) {
          LOGGER.debug("'CKA_VALUE' is not byte[]. Class type is '" + value.getClass().getCanonicalName() + "'");
          continue;
        }
        
        try (ByteArrayInputStream cert = new ByteArrayInputStream((byte[])value)) {
          this.certificates.add(factory.create(cert));
        } catch (CertificateException | IOException e) {
          LOGGER.debug("Unabled to create certificate instance from byte[]", e);
          continue;
        } 
      }
    } finally {
      certificates.sort((a, b) -> a.getAfterDate().compareTo(b.getAfterDate()));
    }
  }
  
  private static boolean hasCertificate(CK_ATTRIBUTE certificate) {
    return certificate.type == PKCS11Constants.CKA_VALUE && certificate.pValue != null;
  }

  private static boolean hasAlias(CK_ATTRIBUTE attribute) {
    return attribute.type == PKCS11Constants.CKA_LABEL && attribute.pValue != null;
  }
}
