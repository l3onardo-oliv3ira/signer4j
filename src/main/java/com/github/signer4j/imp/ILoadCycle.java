package com.github.signer4j.imp;

interface ILoadCycle {
  
  boolean isLoaded();
  
  void unload();
  
  void load();

  void reload();
}
