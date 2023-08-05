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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.github.signer4j.IDevice;
import com.github.signer4j.IDeviceAccessor;
import com.github.signer4j.IDeviceManager;
import com.github.signer4j.IDriverLookupStrategy;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Throwables;

class Signer4JDeviceManager implements IDeviceManager {

  private final IDeviceAccessor pkcs11;
  
  private final IDeviceManager pkcs12;
  
  Signer4JDeviceManager(IDeviceAccessor pkcs11, IDeviceManager pkcs12) {
    this.pkcs11 = Args.requireNonNull(pkcs11, "pkcs11 is null");
    this.pkcs12 = Args.requireNonNull(pkcs12, "pkcs12 is null");
  }
  
  @Override
  public final Repository getRepository() {
    return Repository.NATIVE;
  }
  
  @Override
  public final boolean isLoaded() {
    return pkcs11.isLoaded() && pkcs12.isLoaded();
  }
  
  @Override
  public final void setStrategy(IDriverLookupStrategy strategy) {
    pkcs11.setStrategy(strategy);
  }
  
  @Override
  public final List<IDevice> getDevices() {
    return merge(pkcs11.getDevices(), pkcs12.getDevices());
  }

  @Override
  public final List<IDevice> getDevices(boolean forceReload) {
    return merge(pkcs11.getDevices(forceReload), pkcs12.getDevices(forceReload));
  }

  @Override
  public final List<IDevice> getDevices(Predicate<IDevice> predicate) {
    Args.requireNonNull(predicate, "predicate is null");
    return merge(pkcs11.getDevices(predicate), pkcs12.getDevices(predicate));
  }

  @Override
  public final List<IDevice> getDevices(Predicate<IDevice> predicate, boolean forceReload) {
    Args.requireNonNull(predicate, "predicate is null");
    return merge(pkcs11.getDevices(predicate, forceReload), pkcs12.getDevices(predicate, forceReload));
  }

  @Override
  public final IDeviceManager install(Path... pkcs12Files) {
    pkcs12.install(pkcs12Files);
    return this;
  }

  @Override
  public final IDeviceManager uninstall(Path... pkcs12File) {
    pkcs12.uninstall(pkcs12File);
    return this;
  }

  @Override
  public final IDeviceManager uninstallPkcs12() {
    pkcs12.uninstallPkcs12();
    return this;
  }
  
  @Override
  public final void close() {
    Throwables.runQuietly(pkcs11::close);
    Throwables.runQuietly(pkcs12::close);
  }

  @Override
  public final Optional<IDevice> firstDevice() {
    Optional<IDevice> dev = pkcs11.firstDevice();
    if (dev.isPresent())
      return dev;
    return pkcs12.firstDevice();
  }

  @Override
  public final Optional<IDevice> firstDevice(boolean forceReload) {
    Optional<IDevice> dev = pkcs11.firstDevice(forceReload);
    if (dev.isPresent())
      return dev;
    return pkcs12.firstDevice(forceReload);
  }

  @Override
  public final Optional<IDevice> firstDevice(Predicate<IDevice> predicate) {
    Args.requireNonNull(predicate, "predicate is null");
    return Optional.empty();
  }

  @Override
  public final Optional<IDevice> firstDevice(Predicate<IDevice> predicate, boolean forceReload) {
    Args.requireNonNull(predicate, "predicate is null");
    return Optional.empty();
  }
  
  private static List<IDevice> merge(List<IDevice> pkcs11List, List<IDevice> pkcs12List) {
    List<IDevice> output = new ArrayList<>(pkcs11List.size() + pkcs12List.size());
    output.addAll(pkcs11List);
    output.addAll(pkcs12List);
    return Collections.unmodifiableList(output);
  }
}
