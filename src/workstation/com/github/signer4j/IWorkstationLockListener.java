package com.github.signer4j;

public interface IWorkstationLockListener {

  void onMachineLocked(int value);

  void onMachineUnlocked(int value);
}
