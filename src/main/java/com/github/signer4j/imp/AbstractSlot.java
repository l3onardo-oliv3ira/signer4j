package com.github.signer4j.imp;

import com.github.signer4j.IDevice;
import com.github.signer4j.ISlot;

abstract class AbstractSlot extends ExceptionExpert implements ISlot {

  private final long number;
  
  protected DefaultDevice device;

  protected AbstractSlot(long number) {
    this.number = number;
  }

  @Override
  public final long getNumber() {
    return number;
  }
  
  @Override
  public final IDevice toDevice() {
    return device;
  }

  @Override
  public final String getSerial() {
    return getToken().getSerial();
  }
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + " [description=" + getDescription()
        + ", manufacturerId=" + getManufacturer() + ", hardwareVersion=" + getHardwareVersion()
        + ", firmewareVersion=" + getFirmewareVersion() + ", number="
        + getNumber() + "]";
  }

}