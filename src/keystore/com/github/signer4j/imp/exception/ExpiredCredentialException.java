package com.github.signer4j.imp.exception;

public class ExpiredCredentialException extends Signer4JException {
  
  private static final long serialVersionUID = 1L;
  
  public ExpiredCredentialException(Throwable cause) {
    super("Credenciais expiradas.");
  }
}
