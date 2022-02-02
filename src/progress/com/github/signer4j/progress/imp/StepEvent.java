package com.github.signer4j.progress.imp;

import com.github.signer4j.progress.IState;
import com.github.signer4j.progress.IStepEvent;

class StepEvent extends StateWrapper implements IStepEvent {
  private final String message;
  private final int stackSize;

  StepEvent(IState state, String message, int stackSize) {
    super(state);
    this.message = message;
    this.stackSize = stackSize;
  }

  @Override
  public boolean isIndeterminated() {
    return this.getTotal() < 0;
  }
  
  @Override
  public int getStackSize() {
    return this.stackSize;
  }
  
  @Override
  public String getMessage() {
    return this.message;
  }
  
  @Override
  public String toString() {
    return getStage().beginString() + ": " + getMessage();
  }
}
