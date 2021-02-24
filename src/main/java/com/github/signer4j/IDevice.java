package com.github.signer4j;

public interface IDevice extends IGadget {
  
  enum Type {
    A1, A3, VIRTUAL
  }
  
  Type getType();
  
  String getDriver();
  
  ISlot getSlot();
  
  ICertificates getCertificates();
}
