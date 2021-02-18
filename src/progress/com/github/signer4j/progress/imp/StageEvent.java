package com.github.signer4j.progress.imp;

import com.github.signer4j.progress.IStage;
import com.github.signer4j.progress.IStageEvent;

class StageEvent implements IStageEvent {

  private final IStage stage;
  
  private final String message;

  private final int stackSize;

  StageEvent(IStage stage, int stackSize, String message) {
    this.stage = stage;
    this.stackSize = stackSize;
    this.message = message;
  }

  @Override
  public final int getStackSize() {
    return this.stackSize;
  }
  
  @Override
  public final String getMessage() {
    return this.message;
  }
  
  @Override
  public final IStage getStage() {
    return this.stage;
  }
  
  @Override
  public final String toString() {
    return "[" + stackSize + "]" + this.stage.toString();
  }
}
