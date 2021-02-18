package com.github.signer4j;

public interface ISlot {
  
  long getNumber();
  
  IToken getToken();
  
  IDevice toDevice();

  String getDescription();
  
  String getManufacturerId();
  
  String getHardwareVersion();
  
  String getFirmewareVersion();
  
  String getSerial();
}
