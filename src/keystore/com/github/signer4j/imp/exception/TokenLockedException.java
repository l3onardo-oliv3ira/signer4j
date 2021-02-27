package com.github.signer4j.imp.exception;

public class TokenLockedException extends Signer4JException {
  
  private static final long serialVersionUID = 1L;
  
  public TokenLockedException(String message) {
    super(message);
  }
  
  public TokenLockedException(Throwable cause) {
    super("Token is locked!");
  }

  public TokenLockedException(String message, Throwable cause) {
    super(message, cause);
  }
}
