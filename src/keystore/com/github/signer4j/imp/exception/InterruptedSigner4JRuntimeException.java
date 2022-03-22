package com.github.signer4j.imp.exception;

import java.util.function.Supplier;

public class InterruptedSigner4JRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  
  public static InterruptedSigner4JRuntimeException of(Signer4JException cause) {
    return new InterruptedSigner4JRuntimeException(cause);
  }
  
  public static Supplier<InterruptedSigner4JRuntimeException> lambda(Supplier<Signer4JException> cause) {
    return () -> of(cause.get());
  }
  
  private InterruptedSigner4JRuntimeException(Signer4JException cause) {
    super(cause);
  }
}
