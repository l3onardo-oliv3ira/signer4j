package com.github.signer4j;

import java.nio.file.Path;

public interface IPkcs12Installer {

  IDeviceAccessor install(Path... pkcs12Files);

  IDeviceAccessor uninstall(Path... pkcs12File);

  IDeviceAccessor uninstallPkcs12();
}