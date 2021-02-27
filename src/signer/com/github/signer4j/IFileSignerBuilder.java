package com.github.signer4j;

public interface IFileSignerBuilder<T extends IFileSignerBuilder<T>> {

  T usingSignatureAlgorithm(ISignatureAlgorithm algorithm);

  T usingSignatureType(ISignatureType signatureType);
}