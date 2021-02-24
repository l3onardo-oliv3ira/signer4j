package com.github.signer4j.imp;

abstract class VirtualSlot extends AbstractSlot {

  private String name;
  
  public VirtualSlot(long number, String name) {
    super(number);
    this.name = Args.requireText(name, "name is null").trim();
  }

  @Override
  public final String getDescription() {
    return "Virtual " + name + " Slot";
  }

  @Override
  public final String getManufacturer() {
    return "https://github.com/l3onardo-oliv3ira";
  }

  @Override
  public final String getHardwareVersion() {
    return "Universal";
  }

  @Override
  public final String getFirmewareVersion() {
    return "1.0";
  }
}