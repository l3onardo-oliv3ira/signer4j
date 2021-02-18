package com.github.signer4j.imp;

public class TemporaryException extends Exception {
  
  private static final long serialVersionUID = 7237380113208327295L;

  public TemporaryException(String message) {
    super(message);
  }

  public TemporaryException(Throwable cause) {
    super(cause);
  }

  public TemporaryException(String message, Throwable cause) {
    super(message, cause);
  }
}