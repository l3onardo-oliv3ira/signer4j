package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.github.signer4j.IDevice;
import com.github.signer4j.IDeviceManager;
import com.github.signer4j.IDriver;

public class DeviceManager implements IDeviceManager {

  private boolean loaded = false;
  
  private final PKCS12Driver pk12Driver = PKCS12Driver.getInstance();
  
  private final List<ILifeCycle> drivers = new ArrayList<>();

  private final IDriverLookupStrategy strategy;
  
  private boolean autoDettect;

  public DeviceManager() {
    this(new SmartLookupStrategy(), true);
  }
  
  public DeviceManager(IDriverLookupStrategy strategy) {
    this(strategy, true);
  }
  
  public DeviceManager(IDriverLookupStrategy strategy, boolean autoDettect) {
    this.strategy = Args.requireNonNull(strategy, "strategy is null");
    this.autoDettect = autoDettect;
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
  public final List<IDriver> getDrivers() {
    return getDrivers(!isLoaded());
  }
  
  private List<IDevice> cache;
  
  @Override
  public final List<IDevice> getDevices(boolean forceReload) {
    if (forceReload) {
      reload();
      cache = null;
    }
    if (cache == null) {
      Set<IDevice> devices = new HashSet<>(); //with no duplicated!
      drivers.forEach(d -> devices.addAll(((IDriver)d).getDevices()));
      cache = Collections.unmodifiableList(Containers.toList(devices));
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
  public final List<IDriver> getDrivers(Predicate<IDriver> predicate) {
    return getDrivers().stream().filter(requireNonNull(predicate, "predicate is null")).collect(toList());
  }

  @Override
  public final List<IDriver> getDrivers(Predicate<IDriver> predicate, boolean forceReload) {
    return getDrivers(forceReload).stream().filter(requireNonNull(predicate, "predicate is null")).collect(toList());
  }

  @Override
  public final Optional<IDriver> firstDriver() {
    return getDrivers().stream().findFirst();
  }
  
  @Override
  public final Optional<IDriver> firstDriver(boolean forceReload) {
    return getDrivers(forceReload).stream().findFirst();
  }

  @Override
  public final Optional<IDevice> firstDevice() {
    return getDevices().stream().findFirst();
  }
  
  @Override
  public final Optional<IDevice> firstDevice(boolean forceReload) {
    return getDevices(forceReload).stream().findFirst();
  }
  
  @Override
  public final void uninstall(List<Path> pkcs12Files) {
    requireNonNull(pkcs12Files, "Unabled to uninstall driver with null path's");
    pk12Driver.uninstall(pkcs12Files);
  }
  
  @Override
  public final void uninstallPkcs12() {
    this.pk12Driver.uninstall();
  }
  
  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public final List<IDriver> getDrivers(boolean forceReload) {
    if (forceReload) {
      reload();
    }
    return (List)Collections.unmodifiableList(drivers);
  }
  
  @Override
  public final void install(List<Path> pkcs12File) {
    requireNonNull(pkcs12File, "Unabled to install driver with null path's");
    uninstallPkcs12();
    if (pk12Driver.install(pkcs12File)) {
      install(pk12Driver);
    }
  }
  
  protected final boolean isLoaded() {
    return loaded;
  }
  
  private void reload() {
    unload();
    load();
  }
  
  private void unload() {
    if (isLoaded()) {
      this.drivers.forEach(d -> d.unload());
      this.drivers.clear();
      this.loaded = false;
    }
  }
  
  private void load() {
    if (!isLoaded()) {
      if (autoDettect) {
        strategy.lookup((IDriverSetup setup) -> {
          install(new PKCS11Driver(setup.getLibrary()));
        });
      }
      install(pk12Driver);
      loaded = true;
    }
  }

  private void install(AbstractDriver driver) {
    if (!drivers.contains(driver)) { 
      drivers.add(driver);
    }
  }
}
