package com.github.signer4j.imp;

import java.util.Optional;

import com.github.signer4j.IExceptionHandler;

abstract class ExceptionExpert {

  protected final IExceptionHandler handler;
  
  protected final Runnable dispose;
  
  ExceptionExpert() {
    this(ConsoleExceptionHandler.INSTANCE, () -> {});
  }
  
  ExceptionExpert(IExceptionHandler handler, Runnable dispose) {
    this.handler = Optional.ofNullable(handler).orElseGet(() -> ConsoleExceptionHandler.INSTANCE);
    this.dispose = Args.requireNonNull(dispose, "dispose is null");
  }
  
  protected final void handleException(Throwable e) {
    this.handler.handleException(e);
  }
}
