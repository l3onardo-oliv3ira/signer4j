package com.github.signer4j.imp.exception;

public class PrivateKeyNotFound extends Signer4JException {
  
  private static final long serialVersionUID = 1L;
  
  public PrivateKeyNotFound(String message) {
    super(message);
  }
  
  public PrivateKeyNotFound(String message, Throwable cause) {
    super(message, cause);
  }
}
