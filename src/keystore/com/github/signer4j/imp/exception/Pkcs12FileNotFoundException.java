package com.github.signer4j.imp.exception;

public class Pkcs12FileNotFoundException extends KeyStoreAccessException {

  private static final long serialVersionUID = 1L;

  public Pkcs12FileNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
