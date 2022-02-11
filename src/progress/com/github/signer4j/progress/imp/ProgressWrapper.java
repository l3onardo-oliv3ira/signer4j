package com.github.signer4j.progress.imp;

import java.util.function.Consumer;

import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.IStage;
import com.github.signer4j.progress.IStageEvent;
import com.github.signer4j.progress.IState;
import com.github.signer4j.progress.IStepEvent;

import io.reactivex.Observable;

public class ProgressWrapper implements IProgress {

  protected final IProgress progress;
  
  protected ProgressWrapper(IProgress progress) {
    this.progress = progress;
  }
  
  @Override
  public String getName() {
    return progress.getName();
  }

  @Override
  public void begin(IStage stage) throws InterruptedException {
    progress.begin(stage);
  }

  @Override
  public void begin(IStage stage, int total) throws InterruptedException {
    progress.begin(stage, total);
  }

  @Override
  public void step(String mensagem, Object... params) throws InterruptedException{
    progress.step(mensagem, params);
  }

  @Override
  public void end() throws InterruptedException {
    progress.end();
  }

  @Override
  public <T extends Throwable> T abort(T e) {
    return progress.abort(e);
  }

  @Override
  public boolean isClosed() {
    return progress.isClosed();
  }

  @Override
  public IProgress stackTracer(Consumer<IState> consumer) {
    progress.stackTracer(consumer);
    return this;
  }

  @Override
  public IProgress reset() {
    progress.reset();
    return this;
  }

  @Override
  public Observable<IStepEvent> stepObservable() {
    return progress.stepObservable();
  }

  @Override
  public Observable<IStageEvent> stageObservable() {
    return progress.stageObservable();
  }
  
  @Override
  public Observable<IProgress> disposeObservable() {
    return progress.disposeObservable();
  }
  
  @Override
  public void dispose() {
    progress.dispose();
  }

  @Override
  public Throwable getAbortCause() {
    return progress.getAbortCause();
  }
}
