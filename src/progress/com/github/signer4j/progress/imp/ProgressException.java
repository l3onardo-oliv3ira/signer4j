package com.github.signer4j.progress.imp;

public class ProgressException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ProgressException(String message) {
    super(message);
  }

  public ProgressException(String message, Throwable cause) {
    super(message, cause);
  }
}
