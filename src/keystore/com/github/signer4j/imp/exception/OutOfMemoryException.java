package com.github.signer4j.imp.exception;

public class OutOfMemoryException extends KeyStoreAccessException {
  
  private static final long serialVersionUID = 1L;
  
  public OutOfMemoryException(OutOfMemoryError cause) {
    super("Arquivo muito grande.", cause);
  }
  
  public OutOfMemoryException(String message, OutOfMemoryError cause) {
    super("Out of memory: " + message, cause);
  }
}
