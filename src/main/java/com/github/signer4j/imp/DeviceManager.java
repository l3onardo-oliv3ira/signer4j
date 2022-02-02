package com.github.signer4j.imp;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;

import com.github.signer4j.IDeviceManager;
import com.github.signer4j.IDriverLookupStrategy;

public class DeviceManager extends AbstractDeviceManager {

  private static final PKCS12Driver PKCS12_DRIVER = PKCS12Driver.getInstance();
  
  private final IDriverLookupStrategy strategy;
  
  private boolean autoDettect;

  public DeviceManager() {
    this(new SmartLookupStrategy(), true);
  }
  
  public DeviceManager(IDriverLookupStrategy strategy) {
    this(strategy, true);
  }
  
  public DeviceManager(IDriverLookupStrategy strategy, boolean autoDettect) {
    super(Arrays.asList(PKCS12_DRIVER));
    this.strategy = Args.requireNonNull(strategy, "strategy is null");
    this.autoDettect = autoDettect;
  }
  
  @Override
  public final IDeviceManager uninstall(Path ...pkcs12Files) {
    Args.requireNonNull(pkcs12Files, "Unabled to uninstall driver with null path's");
    PKCS12_DRIVER.uninstall(pkcs12Files);
    return this;
  }
  
  @Override
  public final IDeviceManager uninstallPkcs12() {
    PKCS12_DRIVER.uninstall();
    return this;
  }
  
  @Override
  public final IDeviceManager install(Path ... pkcs12File) {
    Args.requireNonNull(pkcs12File, "Unabled to install driver with null path's");
    uninstallPkcs12();
    PKCS12_DRIVER.install(pkcs12File);
    return this;
  }

  @Override
  protected final void load(Set<IDriver> drivers) {
    if (autoDettect)
      strategy.lookup(setup -> drivers.add(new PKCS11Driver(setup.getLibrary())));
  }
}
