package com.github.signer4j.imp;

import java.util.Optional;

import com.github.signer4j.IExceptionHandler;

abstract class ExceptionExpert {

  private final IExceptionHandler handler;
  
  private final Runnable dispose;
  
  ExceptionExpert() {
    this(() -> {});
  }
  
  ExceptionExpert(Runnable dispose) {
    this(ConsoleExceptionHandler.INSTANCE, dispose);
  }

  ExceptionExpert(IExceptionHandler handler, Runnable dispose) {
    this.handler = Optional.ofNullable(handler).orElseGet(() -> ConsoleExceptionHandler.INSTANCE);
    this.dispose = Args.requireNonNull(dispose, "dispose is null");
  }
  
  protected final void handleException(Throwable e) {
    this.handler.handleException(e);
  }
  
  protected Runnable getDispose() {
    return dispose;
  }
}
