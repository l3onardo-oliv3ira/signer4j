package com.github.signer4j.imp;

import com.github.signer4j.IKeyStoreAccess;

interface IKeyStore extends AutoCloseable, IKeyStoreAccess {
  boolean isClosed();
}
