package com.github.signer4j;

public interface ISlot extends ISerialItem {
  
  long getNumber();
  
  IToken getToken();
  
  IDevice toDevice();

  String getDescription();
  
  String getHardwareVersion();
  
  String getFirmewareVersion();
}
