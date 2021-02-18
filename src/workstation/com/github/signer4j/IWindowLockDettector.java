package com.github.signer4j;

public interface IWindowLockDettector {

  void start();
  
  void stop();

  IWindowLockDettector notifyTo(IWorkstationLockListener listener);
}
