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

import static com.github.utils4j.imp.Args.requireNonNull;
import static com.github.utils4j.imp.Containers.toUnmodifiableList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.github.signer4j.IDevice;
import com.github.signer4j.IDeviceAccessor;
import com.github.utils4j.imp.Containers;

abstract class AbstractDeviceAccessor extends LoadCycle implements IDeviceAccessor {

  private List<IDriver> drivers = Collections.emptyList();

  private List<IDevice> cache;
  
  protected AbstractDeviceAccessor() {
  }

  @Override
  public final void close() {
    unload();
  }
  
  @Override
  public final List<IDevice> getDevices() {
    return getDevices(!isLoaded());
  }
  
  @Override
  public final List<IDevice> getDevices(boolean forceReload) {
    if (forceReload) {
      reload();
      cache = null;
    }
    if (cache == null) {
      cache = toUnmodifiableList(drivers.stream().flatMap(d -> d.getDevices().stream()).collect(toSet()));
    }
    return cache;
  }
  
  @Override
  public final List<IDevice> getDevices(Predicate<IDevice> predicate) {
    return getDevices().stream().filter(requireNonNull(predicate, "predicate is null")).collect(toList());
  }

  @Override
  public final List<IDevice> getDevices(Predicate<IDevice> predicate, boolean forceReload) {
    return getDevices(forceReload).stream().filter(requireNonNull(predicate, "predicate is null")).collect(toList());
  }
  
  @Override
  public final Optional<IDevice> firstDevice() {
    return getDevices().stream().findFirst();
  }
  
  @Override
  public final Optional<IDevice> firstDevice(Predicate<IDevice> predicate) {
    return getDevices().stream().filter(predicate).findFirst();
  }

  @Override
  public final Optional<IDevice> firstDevice(boolean forceReload) {
    return getDevices(forceReload).stream().findFirst();
  }

  @Override
  public final Optional<IDevice> firstDevice(Predicate<IDevice> predicate, boolean forceReload) {
    return getDevices(forceReload).stream().filter(predicate).findFirst();
  }
  
  @Override
  protected final void doUnload()  throws Exception{
    this.drivers.forEach(IDriver::unload);
    this.drivers = Collections.emptyList();
  }
  
  @Override
  protected final void doLoad() throws Exception {
    Set<IDriver> drivers  = new HashSet<>();
    this.load(drivers);
    this.drivers = Containers.toUnmodifiableList(drivers);
  }
  
  protected abstract void load(Set<IDriver> drivers);
}
