package com.github.signer4j.imp.exception;

public class Signer4JException extends Exception {

  private static final long serialVersionUID = 1L;
  
  public Signer4JException(String message) {
    super(message);
  }

  public Signer4JException(String message, Throwable cause) {
    super(message, cause);
  }

  public Signer4JException(Throwable cause) {
    super(cause);
  }
}
