package com.github.signer4j.progress;

public interface IStageEvent {
  
  String getMessage();
  
  int getStackSize();

  IStage getStage();
}