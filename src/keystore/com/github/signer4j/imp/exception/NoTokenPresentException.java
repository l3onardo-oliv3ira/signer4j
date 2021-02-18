package com.github.signer4j.imp.exception;

public class NoTokenPresentException extends KeyStoreAccessException {
  
  private static final long serialVersionUID = 1L;
  
  public NoTokenPresentException(String message) {
    super(message);
  }
  
  public NoTokenPresentException(Throwable cause) {
    super("No token present.");
  }

  public NoTokenPresentException(String message, Throwable cause) {
    super(message, cause);
  }
}
