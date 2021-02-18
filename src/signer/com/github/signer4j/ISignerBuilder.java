package com.github.signer4j;

public interface ISignerBuilder {
  ISignerBuilder usingAlgorigthm(ISignatureAlgorithm algorithm);
  
  ISimpleSigner build();
}
