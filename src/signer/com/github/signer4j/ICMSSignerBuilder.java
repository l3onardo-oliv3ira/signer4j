package com.github.signer4j;

public interface ICMSSignerBuilder extends IFileSignerBuilder<ICMSSignerBuilder> {
  
  ICMSSignerBuilder usingMemoryLimit(long memoryLimit);
  
  ICMSSignerBuilder usingConfig(ICMSConfigSetup config);
  
  ICMSSignerBuilder usingAttributes(boolean hasSignedAttributes);
  
  ICMSSigner build();
}
