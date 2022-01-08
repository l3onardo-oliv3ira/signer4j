package com.github.signer4j.progress;

import java.util.function.Consumer;

import io.reactivex.Observable;

public interface IProgress {
  String PARAM_NAME = IProgress.class.getSimpleName();
  
  void begin(IStage stage);
  
  void begin(IStage stage, int total);
  
  void step(String mensagem, Object... params);
  
  void end();
  
  void abort(Exception e);
  
  boolean isClosed();
  
  IProgress stackTracer(Consumer<IState> consumer);
  
  IProgress reset();
  
  Observable<IStepEvent> stepObservable();

  Observable<IStageEvent> stageObservable();
}
