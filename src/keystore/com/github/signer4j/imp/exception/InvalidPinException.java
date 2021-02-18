package com.github.signer4j.imp.exception;

public class InvalidPinException extends KeyStoreAccessException {
  
  private static final long serialVersionUID = 1L;
  
  public InvalidPinException(Throwable cause) {
    super("Invalid pin!");
  }

  public InvalidPinException(String message, Throwable cause) {
    super(message, cause);
  }
}
