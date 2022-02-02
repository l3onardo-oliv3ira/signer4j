package com.github.signer4j.progress;

import java.util.function.Consumer;

import com.github.signer4j.IDisposable;

import io.reactivex.Observable;

public interface IProgress extends IDisposable {
  
  String CANCELED_OPERATION_MESSAGE = "Operação cancelada!";
  
  String PARAM_NAME = IProgress.class.getSimpleName();
  
  String getName();
  
  void begin(IStage stage) throws InterruptedException;
  
  void begin(IStage stage, int total) throws InterruptedException;
  
  void step(String mensagem, Object... params) throws InterruptedException;
  
  void end() throws InterruptedException;
  
  <T extends Throwable> T abort(T e);
  
  boolean isClosed();
  
  IProgress stackTracer(Consumer<IState> consumer);
  
  IProgress reset();
  
  Observable<IStepEvent> stepObservable();

  Observable<IStageEvent> stageObservable();
  
  Observable<IProgress> disposeObservable();
}
