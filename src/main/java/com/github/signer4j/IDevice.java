package com.github.signer4j;

public interface IDevice extends IGadget {
  
  static enum Type {
    A1, A3
  }
  
  Type getType();
  
  String getDriver();
  
  ISlot getSlot();
  
  ICertificates getCertificates();
}
