package com.github.signer4j.progress;

public interface IState {

  int getStep();

  int getTotal();

  IStage getStage();

  long getTime();

  String getStepTree();

  boolean isAborted();

  Throwable getAbortCause();
}