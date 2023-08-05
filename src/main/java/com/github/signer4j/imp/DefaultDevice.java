/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package com.github.signer4j.imp;

import com.github.signer4j.ICertificates;
import com.github.signer4j.IDevice;
import com.github.signer4j.ISlot;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Strings;

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

    public final Builder withLabel(final String label) {
      this.label = Strings.trim(label);
      return this;
    }

    public final Builder withSlot(final ISlot slot) {
      this.slot = slot;
      return this;
    }
    
    public final Builder withModel(final String model) {
      this.model = Strings.trim(model);
      return this;
    }

    public final Builder withSerial(final String serial) {
      this.serial = Strings.trim(serial);
      return this;
    }
    
    public final Builder withCertificates(final ICertificates certificates) {
      this.certificates = certificates;
      return this;
    }

    final DefaultDevice build() {
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
  public final String getCategory() {
    return getType().toString();
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
  
  @Override
  public final String getManufacturer() {
    return slot.getManufacturer();
  }
  
  final void setCertificates(ICertificates certificates) {
    Args.requireNonNull(certificates, "certificates is null");
    this.certificates = certificates;
  }
  
  @Override
  public final String toString() {
    return String.format("DefaultDevice [driver=%s, label=%s, model=%s, serial=%s, slot=%s, certificates=%s]", driver,
        label, model, serial, slot, certificates);
  }
  
  @Override
  public final int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((serial == null) ? 0 : serial.hashCode());
    return result;
  }

  @Override
  public final boolean equals(Object obj) {
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
