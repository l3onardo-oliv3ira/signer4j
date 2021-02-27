package com.github.signer4j.imp;

import java.security.KeyStore;

import com.github.signer4j.imp.exception.Signer4JException;

interface IKeyStoreBugFixer {
  void fix(KeyStore keystore) throws Signer4JException;
}
