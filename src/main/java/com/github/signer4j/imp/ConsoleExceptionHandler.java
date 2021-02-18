package com.github.signer4j.imp;

import com.github.signer4j.IExceptionHandler;

public enum ConsoleExceptionHandler implements IExceptionHandler {
  INSTANCE;

  @Override
  public void handleException(Throwable e) {
    if (e != null) {
      //e.printStackTrace();
    }
  }
}
