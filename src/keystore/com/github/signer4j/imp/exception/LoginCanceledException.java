package com.github.signer4j.imp.exception;

import com.github.signer4j.progress.IProgress;

public class LoginCanceledException extends Signer4JException {

  private static final String DEFAULT_MESSAGE = IProgress.CANCELED_OPERATION_MESSAGE;
  
  private static final long serialVersionUID = 1L;

  public LoginCanceledException() {
    this(DEFAULT_MESSAGE);
  } 
  
  public LoginCanceledException(String message) {
    super(message);
  }

  public LoginCanceledException(Throwable e) {
    super(DEFAULT_MESSAGE, e);
  }
}
