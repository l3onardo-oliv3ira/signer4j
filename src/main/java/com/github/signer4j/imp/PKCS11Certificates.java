package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.IToken;
import com.github.signer4j.cert.ICertificateFactory;
import com.github.signer4j.exception.DriverException;
import com.github.signer4j.exception.DriverFailException;

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
    this.token = requireNonNull(token, "token is null");
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
          throw new DriverFailException("Unabled to get attribute value from session:  token " + token, e);
        }
        
        CK_ATTRIBUTE aliasAttrib = attributes[0];
        
        if (!hasAlias(aliasAttrib)) {
          LOGGER.warn("Não foi encontrado atributo 'CKA_LABEL' para acesso ao objeto: " + object);
          continue;
        }
        
        attributes = new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(PKCS11Constants.CKA_VALUE) };
        try {
          pk.C_GetAttributeValue(session, object, attributes);
        } catch (PKCS11Exception e3) {
          throw new DriverFailException("Unabled to read CKA_VALUE from token", e3);
        }
        
        CK_ATTRIBUTE certificateAttrib = attributes[0];
        
        if (!hasCertificate(certificateAttrib)) {
          LOGGER.warn("Não foi encontrado valor do atributo 'CKA_VALUE' para acesso ao objeto: " + object);
          continue;
        }

        Object value = certificateAttrib.pValue;
        if (!(value instanceof byte[])) {
          LOGGER.warn("Atributo do certificado encontrado mas não é instância de byte[]. Tipo: " + 
              value.getClass().getCanonicalName());
          continue;
        }
        
        try (ByteArrayInputStream cert = new ByteArrayInputStream((byte[])value)) {
          this.certificates.add(factory.call(cert));
        } catch (CertificateException | IOException e) {
          throw new DriverFailException("Unabled to create certificate from inputstream", e);
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
