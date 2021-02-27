package com.github.signer4j.imp.exception;

public class InvalidPinException extends Signer4JException {
  
  private static final long serialVersionUID = 1L;
  
  public InvalidPinException(Throwable cause) {
    super("Invalid pin!");
  }

  public InvalidPinException(String message, Throwable cause) {
    super(message, cause);
  }
}
