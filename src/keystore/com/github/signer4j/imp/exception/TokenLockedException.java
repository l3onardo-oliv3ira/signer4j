package com.github.signer4j.imp.exception;

public class TokenLockedException extends Signer4JException {
  
  private static final long serialVersionUID = 1L;
  
  public TokenLockedException(Throwable cause) {
    super("O token está bloqueado.");
  }
}
