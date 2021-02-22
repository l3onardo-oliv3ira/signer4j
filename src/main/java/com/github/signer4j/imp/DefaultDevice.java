package com.github.signer4j.imp;

import com.github.signer4j.ICertificates;
import com.github.signer4j.IDevice;
import com.github.signer4j.ISlot;

class DefaultDevice implements IDevice {

  static class Builder {

    private String driver;
    private String label;
    private String model;
    private String serial;
    private ISlot slot;
    private Type type;
    private ICertificates certificates;
    
    Builder(Type type) {
      this.type = Args.requireNonNull(type,  "type is null");
    }

    public final Builder withDriver(final String driver) {
      this.driver = Strings.trim(driver);
      return this;
    }

    public Builder withLabel(final String label) {
      this.label = Strings.trim(label);
      return this;
    }

    public Builder withSlot(final ISlot slot) {
      this.slot = slot;
      return this;
    }
    
    public Builder withModel(final String model) {
      this.model = Strings.trim(model);
      return this;
    }

    public Builder withSerial(final String serial) {
      this.serial = Strings.trim(serial);
      return this;
    }
    
    public Builder withCertificates(final ICertificates certificates) {
      this.certificates = certificates;
      return this;
    }

    DefaultDevice build() {
      DefaultDevice device = new DefaultDevice();
      device.driver = driver;
      device.label = label;
      device.model = model;
      device.serial = serial;
      device.slot = slot;
      device.type = type;
      device.certificates = this.certificates;
      return device;
    }
  }

  private String driver;
  private String label;
  private String model;
  private String serial;
  private ISlot slot;
  private Type type;
  
  private ICertificates certificates;

  public DefaultDevice() {
    this.driver = "";
    this.label = "";
    this.model = "";
    this.serial = "";
    this.slot = null;
    this.type = null;
    this.certificates = Unavailables.getCertificates(null);
  }

  @Override
  public final Type getType() {
    return type;
  }
  
  @Override
  public final String getDriver() {
    return this.driver;
  }

  @Override
  public final String getLabel() {
    return this.label;
  }

  @Override
  public final ISlot getSlot() {
    return this.slot;
  }

  @Override
  public final String getModel() {
    return this.model;
  }

  @Override
  public final String getSerial() {
    return this.serial;
  }

  @Override
  public final ICertificates getCertificates() {
    return this.certificates;
  }
  
  final void setCertificates(ICertificates certificates) {
    Args.requireNonNull(certificates, "certificates is null");
    this.certificates = certificates;
  }

  @Override
  public String toString() {
    return String.format("DefaultDevice [driver=%s, label=%s, model=%s, serial=%s, slot=%s, certificates=%s]", driver,
        label, model, serial, slot, certificates);
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((serial == null) ? 0 : serial.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DefaultDevice other = (DefaultDevice) obj;
    if (serial == null) {
      if (other.serial != null)
        return false;
    } else if (!serial.equals(other.serial))
      return false;
    return true;
  }
}
