package com.github.signer4j;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface IDeviceManager {
  
  List<IDevice> getDevices();
  
  List<IDevice> getDevices(boolean forceReload);
  
  List<IDevice> getDevices(Predicate<IDevice> predicate);

  List<IDevice> getDevices(Predicate<IDevice> predicate, boolean forceReload);
  
  Optional<IDevice> firstDevice();

  Optional<IDevice> firstDevice(boolean forceReload);

  List<IDriver> getDrivers();
  
  List<IDriver> getDrivers(boolean forceReload);

  List<IDriver> getDrivers(Predicate<IDriver> predicate);

  List<IDriver> getDrivers(Predicate<IDriver> predicate, boolean forceReload);
  
  Optional<IDriver> firstDriver();

  Optional<IDriver> firstDriver(boolean forceReload);

  void install(List<Path> pkcs12Files);

  void uninstall(List<Path> pkcs12File);
  
  void uninstallPkcs12();

  void close();
}
