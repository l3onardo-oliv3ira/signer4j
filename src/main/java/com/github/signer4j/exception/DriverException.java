package com.github.signer4j.exception;

public abstract class DriverException extends Exception {
  
  private static final long serialVersionUID = 1L;

  public DriverException(String message, Throwable cause) {
    super(message, cause);
  }
}
