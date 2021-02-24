package com.github.signer4j;

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

  Optional<IDevice> firstDevice(Predicate<IDevice> predicate);

  Optional<IDevice> firstDevice(Predicate<IDevice> predicate, boolean forceReload);
 
  void close();
}
