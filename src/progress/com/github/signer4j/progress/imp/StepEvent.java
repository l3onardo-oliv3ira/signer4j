package com.github.signer4j.progress.imp;

import com.github.signer4j.progress.IStage;
import com.github.signer4j.progress.IState;
import com.github.signer4j.progress.IStepEvent;

class StepEvent implements IStepEvent {
  private final IState state;
  private final String message;
  private final int stackSize;

  StepEvent(IState state, String message, int stackSize) {
    this.state = state;
    this.message = message;
    this.stackSize = stackSize;
  }

  @Override
  public int getStep() {
    return this.state.getStep();
  }
  
  @Override
  public String getStepTree() {
    return this.state.getStepTree();
  }

  @Override
  public int getTotal() {
    return this.state.getTotal();
  }

  @Override
  public int getStackSize() {
    return this.stackSize;
  }
  
  @Override
  public IStage getStage() {
    return this.state.getStage();
  }

  @Override
  public long getTime() {
    return this.state.getTime();
  }

  @Override
  public String getMessage() {
    return this.message;
  }
  
  @Override
  public String toString() {
    return getStage().beginString() + ": " + getMessage();
  }

  @Override
  public boolean isAborted() {
    return state.isAborted();
  }
}
