package com.github.signer4j.imp;

import com.github.signer4j.IWindowLockDettector;
import com.github.signer4j.IWorkstationLockListener;

public enum WindowLockDettector implements IWindowLockDettector {
  IDLE,
  LINUX, 
  MAC,
  WINDOWS() {
    private final IWindowLockDettector forWindows = new ForWindowsLockDettector();
    
    @Override
    public void start() {
      forWindows.start();
    }

    @Override
    public void stop() {
      forWindows.stop();
    }
    
    @Override
    public IWindowLockDettector notifyTo(IWorkstationLockListener listener) {
      forWindows.notifyTo(listener);
      return this;
    }
  };
  
  public static IWindowLockDettector getBest() {
    return SystemSupport.getDefault().getWindowLockDettector();
  }
  
  @Override
  public void start() {
    ;//linux and mac not implemented yet
  }

  @Override
  public void stop() {
    ;//linux and mac not implemented yet
  }

  @Override
  public IWindowLockDettector notifyTo(IWorkstationLockListener listener) {
    ;//linux and mac not implemented yet
    return this;
  }
}
