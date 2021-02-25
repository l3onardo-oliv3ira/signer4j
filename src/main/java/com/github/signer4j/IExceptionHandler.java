package com.github.signer4j;

import org.apache.log4j.Logger;

public interface IExceptionHandler {
  Logger LOGGER = Logger.getLogger(IExceptionHandler.class);
  
  void handleException(Throwable e);
}
