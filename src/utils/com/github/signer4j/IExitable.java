package com.github.signer4j;

public interface IExitable {
  default void exit() {
    exit(0);
  }
  
  void exit(long delay);
}
