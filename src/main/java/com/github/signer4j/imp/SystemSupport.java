package com.github.signer4j.imp;

import com.github.signer4j.IWindowLockDettector;

enum SystemSupport implements IModuleExtension {
  WINDOWS(".dll") {
    @Override
    protected IDriverLookupStrategy createDriverLookupStrategy() {
      return new ForWindowsStrategy();
    }

    @Override
    protected IWindowLockDettector getWindowLockDettector() {
      return WindowLockDettector.WINDOWS;
    }
  },
  
  LINUX(".so") {
    @Override
    protected IDriverLookupStrategy createDriverLookupStrategy() {
      return new ForLinuxStrategy();
    }

    @Override
    protected IWindowLockDettector getWindowLockDettector() {
      return WindowLockDettector.LINUX;
    }
  },
  
  MAC(".dylib") {
    @Override
    protected IDriverLookupStrategy createDriverLookupStrategy() {
      return new ForMacStrategy();
    }
    
    @Override
    protected IWindowLockDettector getWindowLockDettector() {
      return WindowLockDettector.MAC;
    }

  },
  
  UNKNOWN(".unknown") {
    @Override
    protected IDriverLookupStrategy createDriverLookupStrategy() {
      return IDriverLookupStrategy.IDLE;
    }
    
    @Override
    protected IWindowLockDettector getWindowLockDettector() {
      return WindowLockDettector.IDLE;
    }
  };

  private static SystemSupport SYSTEM;
  
  public static SystemSupport getDefault() {
    return SYSTEM != null ? SYSTEM : ( SYSTEM = (
        Jvms.isWindows() ? WINDOWS : (
        Jvms.isUnix() ? LINUX: (
        Jvms.isMac() ? MAC : UNKNOWN
    ))));
  }

  private final String extension;

  private IDriverLookupStrategy finder;
  
  SystemSupport(String extension) {
    this.extension = extension;
  }
  
  @Override
  public String getExtension() {
    return extension;
  }

  public final IDriverLookupStrategy getStrategy() {
    return finder == null ? finder = createDriverLookupStrategy() : finder;
  }

  protected abstract IDriverLookupStrategy createDriverLookupStrategy();

  protected abstract IWindowLockDettector getWindowLockDettector();
}
