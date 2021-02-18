package com.github.signer4j.imp;

import java.security.KeyStore;

import com.github.signer4j.imp.exception.KeyStoreAccessException;

interface IKeyStoreBugFixer {
  void fix(KeyStore keystore) throws KeyStoreAccessException;
}
