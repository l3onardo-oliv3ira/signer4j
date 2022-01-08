package com.github.signer4j.progress;

import java.util.function.Consumer;

import com.github.signer4j.IDisposable;

import io.reactivex.Observable;

public interface IProgress extends IDisposable {
  
  String PARAM_NAME = IProgress.class.getSimpleName();
  
  String getName();
  
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
  
  Observable<IProgress> disposeObservable();
}
