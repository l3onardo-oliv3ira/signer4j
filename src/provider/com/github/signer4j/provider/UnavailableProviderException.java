package com.github.signer4j.provider;

import java.security.ProviderException;

public class UnavailableProviderException extends ProviderException {

  private static final long serialVersionUID = 3552588314412255123L;

  public UnavailableProviderException(String message) {
    super(message);
  }

  public UnavailableProviderException(Throwable cause) {
    super(cause);
  }
  
  public UnavailableProviderException(String message, Throwable cause) {
    super(message, cause);
  }
}
