package com.github.signer4j.progress.imp;

public class InterruptedProgress extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public InterruptedProgress(String message) {
    super(message);
  }

  public InterruptedProgress(String message, Throwable cause) {
    super(message, cause);
  }
}
