package com.github.signer4j.imp.exception;

public class CertificateAliasNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  
  public CertificateAliasNotFoundException() {
    super("Alias não definido para o certificado escolhido! Certifique-se de que o certificado possua uma chave privada associada a ele.");
  }
}
