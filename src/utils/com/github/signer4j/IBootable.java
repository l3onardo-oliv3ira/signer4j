package com.github.signer4j;

public interface IBootable {

  String getOrigin();
  
  void boot();
  
  void logout();

  void exit(long delay);

  default void exit() {
    exit(0);
  }
}
