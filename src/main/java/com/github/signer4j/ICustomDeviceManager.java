package com.github.signer4j;

import java.nio.file.Path;

public interface ICustomDeviceManager extends IDeviceManager {
  
  void install(Path ... pkcs12Files);

  void uninstall(Path... pkcs12File);
  
  void uninstallPkcs12();
}
