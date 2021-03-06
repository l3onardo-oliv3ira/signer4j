package com.github.signer4j.imp.exception;

public class Signer4JRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  
  public Signer4JRuntimeException(Signer4JException cause) {
    super(cause);
  }
}
