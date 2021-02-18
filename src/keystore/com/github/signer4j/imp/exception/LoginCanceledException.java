package com.github.signer4j.imp.exception;

public class LoginCanceledException extends KeyStoreAccessException {

  private static final String DEFAULT_MESSAGE = "login canceled!";
  
  private static final long serialVersionUID = 1L;

  public LoginCanceledException() {
    super(DEFAULT_MESSAGE);
  } 
  
  public LoginCanceledException(Throwable e) {
    super(DEFAULT_MESSAGE, e);
  }
}
