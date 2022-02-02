package com.github.signer4j.progress;

public interface IStageEvent {
  
  String getMessage();
  
  int getStackSize();

  int getTotal();

  IStage getStage();

  boolean isIndeterminated();

  boolean isEnd();
  
  boolean isStart();
}