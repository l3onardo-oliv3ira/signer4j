package com.github.signer4j.exception;

public class NotAuthenticatedException extends IllegalStateException {

  private static final long serialVersionUID = 1L;

  public NotAuthenticatedException(String message) {
    super(message);
  }
}
