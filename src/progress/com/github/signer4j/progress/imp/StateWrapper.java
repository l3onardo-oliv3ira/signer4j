package com.github.signer4j.progress.imp;

import com.github.signer4j.progress.IStage;
import com.github.signer4j.progress.IState;

public class StateWrapper implements IState {

  private final IState state;
  
  public StateWrapper(IState state) {
    this.state = state;
  }
  
  @Override
  public int getStep() {
    return state.getStep();
  }

  @Override
  public int getTotal() {
    return state.getTotal();
  }

  @Override
  public IStage getStage() {
    return state.getStage();
  }

  @Override
  public long getTime() {
    return state.getTime();
  }

  @Override
  public String getStepTree() {
    return state.getStepTree();
  }

  @Override
  public boolean isAborted() {
    return state.isAborted();
  }

  @Override
  public Throwable getAbortCause() {
    return state.getAbortCause();
  }
}
