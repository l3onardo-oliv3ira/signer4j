package com.github.signer4j.progress.imp;

import com.github.signer4j.progress.IStageEvent;
import com.github.signer4j.progress.IState;

class StageEvent extends StateWrapper implements IStageEvent {

  private final String message;

  private final int stackSize;

  private final boolean end;

  StageEvent(IState state, String message, int stackSize, boolean end) {
    super(state);
    this.stackSize = stackSize;
    this.message = message;
    this.end = end;
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
  public final boolean isIndeterminated() {
    return this.getTotal() < 0;
  }
  
  @Override
  public final boolean isEnd() {
    return this.end;
  }
  
  @Override
  public final boolean isStart() {
    return !end;
  }

  @Override
  public final String toString() {
    return "[" + stackSize + "]" + this.getStage().toString();
  }

}
