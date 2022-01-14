package com.github.signer4j;

public interface IChoice extends IPersonalData {
  
  boolean isCanceled();

  String getProvider();
  
}
