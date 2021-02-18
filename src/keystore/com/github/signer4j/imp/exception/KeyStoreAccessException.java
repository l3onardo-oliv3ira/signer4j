package com.github.signer4j.imp.exception;

public class KeyStoreAccessException extends Exception {

  private static final long serialVersionUID = 1L;
  
  public KeyStoreAccessException(String message) {
    super(message);
  }

  public KeyStoreAccessException(String message, Throwable cause) {
    super(message, cause);
  }

  public KeyStoreAccessException(Throwable cause) {
    super(cause);
  }
}
