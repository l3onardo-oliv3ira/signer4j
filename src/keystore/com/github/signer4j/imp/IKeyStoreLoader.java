package com.github.signer4j.imp;

import com.github.signer4j.IKeyStoreAccess;
import com.github.signer4j.IParams;
import com.github.signer4j.imp.exception.KeyStoreAccessException;

interface IKeyStoreLoader {
  IKeyStoreAccess getKeyStore(IParams params) throws KeyStoreAccessException;
}