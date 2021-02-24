package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;
import static com.github.signer4j.imp.Containers.toUnmodifiableList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.github.signer4j.IDevice;
import com.github.signer4j.IDeviceManager;

abstract class AbstractDeviceManager extends LoadCycle implements IDeviceManager {

  private List<IDriver> drivers;

  private List<IDriver> defaults;
  
  private List<IDevice> cache;
  
  AbstractDeviceManager() {
    this(Collections.emptyList());
  }

  AbstractDeviceManager(List<IDriver> defaults) {
    this.defaults = new ArrayList<>(Args.requireNonNull(defaults, "defaults is null"));
    this.drivers = Collections.unmodifiableList(defaults);
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
    this.drivers.forEach(d -> d.unload());
    this.drivers.clear();
  }
  
  @Override
  protected final void doLoad() throws Exception {
    Set<IDriver> drivers  = new HashSet<>();
    this.load(drivers);
    drivers.addAll(defaults);
    this.drivers = Containers.toUnmodifiableList(drivers);
  }
  
  protected abstract void load(Set<IDriver> drivers);
}
