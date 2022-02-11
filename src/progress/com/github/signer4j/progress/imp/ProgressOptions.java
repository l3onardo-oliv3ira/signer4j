package com.github.signer4j.progress.imp;

import java.util.function.Consumer;

import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.IProgressView;
import com.github.signer4j.progress.IStage;
import com.github.signer4j.progress.IStageEvent;
import com.github.signer4j.progress.IState;
import com.github.signer4j.progress.IStepEvent;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public enum ProgressOptions implements IProgressView {
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
  public <T extends Throwable> T abort(T e) {
    return e;
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
  public IProgressView reset() {
    return this;
  }

  @Override
  public void display() {
    
  }

  @Override
  public void undisplay() {
    
  }

  @Override
  public void dispose() {
    
  }

  @Override
  public BehaviorSubject<IProgress> disposeObservable() {
    return null;
  }

  @Override
  public String getName() {
    return IDLE.getName();
  }

  @Override
  public void cancelCode(Runnable cancelCode) {
    
  }

  @Override
  public Throwable getAbortCause() {
    return null;
  }
}
