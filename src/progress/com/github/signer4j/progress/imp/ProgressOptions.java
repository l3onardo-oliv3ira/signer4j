package com.github.signer4j.progress.imp;

import java.util.function.Consumer;

import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.IStage;
import com.github.signer4j.progress.IStageEvent;
import com.github.signer4j.progress.IState;
import com.github.signer4j.progress.IStepEvent;

import io.reactivex.Observable;

public enum ProgressOptions implements IProgress {
  IDLE; 
  
  @Override
  public void begin(IStage stage) {
  }
  
  @Override
  public void begin(IStage stage, int total) {
  }

  @Override
  public void step(String mensagem, Object ... params) {
  }

  @Override
  public void end() {
  }

  @Override
  public void abort(Exception e) {
  }

  @Override
  public Observable<IStepEvent> stepObservable() {
    return Observable.empty();
  }

  @Override
  public Observable<IStageEvent> stageObservable() {
    return Observable.empty();
  }
  
  @Override
  public IProgress stackTracer(Consumer<IState> consumer) {
    return this;
  }

  @Override
  public boolean isClosed() {
    return false;
  }

  @Override
  public void dispose() {
  }

  @Override
  public IProgress reset(Runnable dispose) {
    return this;
  }

  @Override
  public void applyThread() {
    // TODO Auto-generated method stub
  }

  @Override
  public IProgress setThread(Consumer<Thread> setter) {
    return this;
  }
}
