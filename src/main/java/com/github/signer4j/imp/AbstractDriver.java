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

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.signer4j.IDevice;
import com.github.signer4j.ILibraryAware;
import com.github.signer4j.ISlot;
import com.github.signer4j.IToken;
import com.github.signer4j.exception.DriverException;

abstract class AbstractDriver extends LoadCycle implements IDriver {

  private List<ISlot> slots = emptyList();

  private final List<IDevice> devices = new ArrayList<>();
  
  protected AbstractDriver() {
  }
  
  @Override
  public String toString() {
    return getId();
  }
  
  protected boolean isSame(AbstractDriver obj) {
    return getId().equals(obj.getId());
  }
  
  @Override
  public final Optional<IToken> firstToken() {
    return slots.size() == 0 ? empty() : ofNullable(slots.get(0).getToken());
  }
  
  @Override
  public final boolean isLibraryAware() {
    return this instanceof ILibraryAware;
  }
  
  protected final void addDevice(IDevice device) {
    if (device != null) {
      this.devices.add(device);
    }
  }

  @Override
  public final List<IDevice> getDevices() {
    load();
    return Collections.unmodifiableList(devices);
  }
  
  @Override
  public final List<ISlot> getSlots() {
    load();
    return this.slots;
  }
  
  @Override
  public final int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + getId().hashCode();
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
    return isSame((AbstractDriver)obj);
  }
  
  @Override
  protected final void doUnload() {
    try {
      this.slots.stream().map(ISlot::getToken).forEach(IToken::logout);
    }finally {
      this.devices.clear();
      this.slots = emptyList(); 
    }
  }

  @Override
  protected final void doLoad() throws Exception {
    List<ISlot> slots = new ArrayList<>();
    this.loadSlots(slots);
    this.slots = Collections.unmodifiableList(slots);
  }

  protected abstract void loadSlots(List<ISlot> output) throws DriverException;
}
