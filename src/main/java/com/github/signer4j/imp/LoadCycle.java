package com.github.signer4j.imp;

abstract class LoadCycle extends ExceptionExpert implements ILoadCycle {

  private boolean loaded = false;
  
  @Override
  public final boolean isLoaded() {
    return loaded;
  }
  
  @Override
  public final void reload() {
    unload();
    load();
  }
  
  @Override
  public final void unload() {
    if (isLoaded()) {
      try {
        this.doUnload();
      }catch(Exception e) {
        handleException(e);
      }finally {
        this.loaded = false;
      }
    }
  }

  @Override
  public final void load() {
    if (!isLoaded()) {
      try {
        doLoad();
      }catch(Exception e) {
        handleException(e);
      }finally {
        loaded = true;
      }
    }
  }
  
  protected abstract void doUnload() throws Exception;
  
  protected abstract void doLoad() throws Exception;
}
