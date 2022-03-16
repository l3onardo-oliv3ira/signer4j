package com.github.signer4j.imp.exception;

public class NoTokenPresentException extends Signer4JException {
  
  private static final long serialVersionUID = 1L;
  
  public NoTokenPresentException(Throwable cause) {
    super("O token/certificado não está presente.", cause);
  }
}
