package com.github.signer4j;

public interface IFinishable {
  default void finish() {
    finish(0);
  }
  
  void finish(long delay);
}
