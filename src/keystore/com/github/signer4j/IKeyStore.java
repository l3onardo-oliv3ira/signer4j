package com.github.signer4j;

import java.util.Enumeration;

import com.github.signer4j.imp.exception.KeyStoreAccessException;

public interface IKeyStore extends AutoCloseable, IKeyStoreAccess {
  
  boolean isClosed();
  
  Enumeration<String> getAliases() throws KeyStoreAccessException;
 
}
