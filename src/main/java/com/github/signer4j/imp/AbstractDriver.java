package com.github.signer4j.imp;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.signer4j.IDevice;
import com.github.signer4j.IDriver;
import com.github.signer4j.ILibraryAware;
import com.github.signer4j.ISlot;
import com.github.signer4j.IToken;
import com.github.signer4j.exception.DriverException;

abstract class AbstractDriver extends ExceptionExpert implements IDriver, ILifeCycle {

  private boolean loaded;

  private List<ISlot> slots = unmodifiableList(emptyList());

  private final List<IDevice> devices = new ArrayList<>();
  
  protected AbstractDriver() {
    this.loaded = false;
  }
  
  @Override
  public final boolean isLoaded() {
    return this.loaded;
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
  
  @Override
  public final void reload() {
    unload();
    load();
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
  public final void unload() {
    if (isLoaded()) {
      try {
        this.slots.stream().map(s -> s.getToken()).forEach(t -> t.logout());
      }finally {
        this.loaded = false;
        this.devices.clear();
        this.slots = unmodifiableList(emptyList()); 
      }
    }
  }

  @Override
  public final void load() {
    if (!isLoaded()) {
      List<ISlot> slots = new ArrayList<>();
      try {
        doLoad(slots);
      } catch (DriverException e) {
        handleException(e);
      }
      this.slots = Collections.unmodifiableList(slots);
      loaded = true;
    }
  }

  protected abstract void doLoad(List<ISlot> output) throws DriverException;
}
