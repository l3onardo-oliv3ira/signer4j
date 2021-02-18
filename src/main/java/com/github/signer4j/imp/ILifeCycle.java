package com.github.signer4j.imp;

interface ILifeCycle {
  
  boolean isLoaded();
  
  void unload();
  
  void load();

  void reload();
}
