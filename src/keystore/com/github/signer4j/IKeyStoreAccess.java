package com.github.signer4j;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.List;

import com.github.signer4j.imp.exception.KeyStoreAccessException;

public interface IKeyStoreAccess {
  
  IDevice getDevice();
  
  String getProvider() throws KeyStoreAccessException;

  Certificate getCertificate(String alias) throws KeyStoreAccessException;

  List<Certificate> getCertificateChain(String alias) throws KeyStoreAccessException;

  String getCertificateAlias(Certificate certificate) throws KeyStoreAccessException;

  PrivateKey getPrivateKey(String alias, char[] password) throws KeyStoreAccessException;

  default PrivateKey getPrivateKey(String alias) throws KeyStoreAccessException {
    return getPrivateKey(alias, null);
  }
}