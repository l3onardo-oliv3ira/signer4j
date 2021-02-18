package com.github.signer4j;

import java.util.List;
import java.util.Optional;

public interface IDriver {
  
  String getId();
  
  List<ISlot> getSlots();
  
  List<IDevice> getDevices();
  
  boolean isLibraryAware();

  Optional<IToken> firstToken();
}
