package com.github.signer4j.imp;

import java.util.List;
import java.util.Optional;

import com.github.signer4j.IDevice;
import com.github.signer4j.ISlot;
import com.github.signer4j.IToken;

interface IDriver extends ILoadCycle {
  
  String getId();
  
  List<ISlot> getSlots();
  
  List<IDevice> getDevices();
  
  boolean isLibraryAware();

  Optional<IToken> firstToken();
}
