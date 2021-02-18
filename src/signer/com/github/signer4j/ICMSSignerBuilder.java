package com.github.signer4j;


public interface ICMSSignerBuilder {
  ICMSSignerBuilder usingMemoryLimit(long memoryLimit);
  
  ICMSSignerBuilder usingAlgorigthm(ISignatureAlgorithm algorithm);
  
  ICMSSignerBuilder usingSignatureType(ISignatureType signatureType);

  ICMSSignerBuilder usingAttributes(boolean hasSignedAttributes);
  
  ICMSSigner build();
}
