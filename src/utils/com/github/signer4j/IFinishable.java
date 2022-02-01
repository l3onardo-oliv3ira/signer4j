package com.github.signer4j;

public interface IFinishable {
  default void exit() {
    exit(0);
  }
  
  void exit(long delay);

  void logout();
}
