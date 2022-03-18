package com.github.signer4j.imp;

public class CanceledOperationException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public CanceledOperationException() {
    super("Operação cancelada.");
  }
}
