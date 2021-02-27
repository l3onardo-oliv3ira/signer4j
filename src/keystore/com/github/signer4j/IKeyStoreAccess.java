package com.github.signer4j;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import com.github.signer4j.imp.exception.Signer4JException;

public interface IKeyStoreAccess {
  
  Optional<IDevice> getDevice();
  
  String getProvider() throws Signer4JException;

  Enumeration<String> getAliases() throws Signer4JException;
  
  Certificate getCertificate(String alias) throws Signer4JException;

  List<Certificate> getCertificateChain(String alias) throws Signer4JException;

  String getCertificateAlias(Certificate certificate) throws Signer4JException;

  PrivateKey getPrivateKey(String alias, char[] password) throws Signer4JException;

  default PrivateKey getPrivateKey(String alias) throws Signer4JException {
    return getPrivateKey(alias, null);
  }
}