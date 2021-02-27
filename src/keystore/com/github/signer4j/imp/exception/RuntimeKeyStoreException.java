package com.github.signer4j.imp.exception;

public class RuntimeKeyStoreException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  
  public RuntimeKeyStoreException(Signer4JException cause) {
    super(cause);
  }
}
