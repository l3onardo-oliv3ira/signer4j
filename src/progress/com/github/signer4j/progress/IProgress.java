package com.github.signer4j.progress;

import java.util.function.Consumer;

import io.reactivex.Observable;

public interface IProgress {
  String PROGRESS_PARAM = IProgress.class.getSimpleName();
  
  void begin(IStage stage);
  
  void begin(IStage stage, int total);
  
  void step(String mensagem, Object... params);
  
  void end();
  
  void abort(Exception e);
  
  IProgress stackTracer(Consumer<IState> consumer);
  
  boolean isClosed();
  
  IProgress reset(Runnable gameData);
  
  Observable<IStepEvent> stepObservable();

  Observable<IStageEvent> stageObservable();
  
  void gameOver();

  default void reset() {
    reset(() -> {});
  }
}
