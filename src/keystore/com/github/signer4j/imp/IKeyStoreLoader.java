package com.github.signer4j.imp;

import com.github.signer4j.IKeyStoreAccess;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.IParams;

interface IKeyStoreLoader {
  IKeyStoreAccess getKeyStore(IParams params) throws Signer4JException;
}