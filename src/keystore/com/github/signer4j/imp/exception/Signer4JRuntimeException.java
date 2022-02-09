package com.github.signer4j.imp.exception;

import java.util.function.Supplier;

public class Signer4JRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  
  public static Signer4JRuntimeException of(Signer4JException cause) {
    return new Signer4JRuntimeException(cause);
  }
  
  public static Supplier<Signer4JRuntimeException> lambda(Signer4JException cause) {
    return () -> of(cause);
  }
  
  private Signer4JRuntimeException(Signer4JException cause) {
    super(cause);
  }
}
