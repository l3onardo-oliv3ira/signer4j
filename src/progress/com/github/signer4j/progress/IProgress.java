package com.github.signer4j.progress;

import java.util.function.Consumer;

import com.github.signer4j.IDisposable;

import io.reactivex.Observable;

public interface IProgress extends IDisposable {
  String PROGRESS_PARAM = IProgress.class.getSimpleName();
  
  void begin(IStage stage);
  
  void begin(IStage stage, int total);
  
  void step(String mensagem, Object... params);
  
  void end();
  
  void abort(Exception e);
  
  void applyThread();
  
  boolean isClosed();
  
  IProgress stackTracer(Consumer<IState> consumer);
  
  IProgress reset(Runnable dispose);
  
  IProgress setThread(Consumer<Thread> setter);
  
  Observable<IStepEvent> stepObservable();

  Observable<IStageEvent> stageObservable();
}
