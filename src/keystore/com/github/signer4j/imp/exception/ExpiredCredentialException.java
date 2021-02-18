package com.github.signer4j.imp.exception;

public class ExpiredCredentialException extends KeyStoreAccessException {
  
  private static final long serialVersionUID = 1L;
  
  public ExpiredCredentialException(String message) {
    super(message);
  }
  
  public ExpiredCredentialException(Throwable cause) {
    super("Credential expired!");
  }

  public ExpiredCredentialException(String message, Throwable cause) {
    super(message, cause);
  }
}
