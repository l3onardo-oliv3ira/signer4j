package com.github.signer4j.imp.exception;

public class ModuleException extends KeyStoreAccessException {
  
  private static final long serialVersionUID = 1L;
  
  public ModuleException(String message) {
    super(message);
  }
  
  public ModuleException(Throwable cause) {
    super(cause);
  }
  
  public ModuleException(String message, Throwable cause) {
    super(message, cause);
  }
}
