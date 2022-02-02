package com.github.signer4j.progress;

public interface IStepEvent extends IState {
  
  String getMessage();
  
  int getStackSize();
  
  String getStepTree();

  boolean isIndeterminated();
}