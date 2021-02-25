package com.github.signer4j.imp;

import java.util.Optional;

import com.github.signer4j.IExceptionHandler;

abstract class ExceptionExpert {

  private final IExceptionHandler handler;
  
  ExceptionExpert() {
    this(ConsoleExceptionHandler.INSTANCE);
  }

  ExceptionExpert(IExceptionHandler handler) {
    this.handler = Optional.ofNullable(handler).orElseGet(() -> ConsoleExceptionHandler.INSTANCE);
  }
  
  protected final void handleException(Throwable e) {
    this.handler.handleException(e);
  }
}
