package com.github.signer4j.progress.imp;

import java.util.function.Consumer;

import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.Ids;
import com.github.signer4j.imp.Stack;
import com.github.signer4j.imp.Throwables;
import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.IStage;
import com.github.signer4j.progress.IStageEvent;
import com.github.signer4j.progress.IState;
import com.github.signer4j.progress.IStepEvent;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class DefaultProgress implements IProgress {

  private boolean closed = false;

  private final Stack<State> stack = new Stack<State>();
  
  private BehaviorSubject<IStepEvent> stepSubject;
  
  private BehaviorSubject<IStageEvent> stageSubject;
  
  private BehaviorSubject<IProgress> disposeSubject;
  
  private final String name;
  
  public DefaultProgress() {
    this(Ids.next());
  }

  public DefaultProgress(String name) {
    this.name = Args.requireText(name, "name can't be null");
    this.resetObservables();
  }

  @Override
  public final String getName() {
    return name;
  }
  
  @Override
  public final void begin(IStage stage) throws InterruptedException {
    begin(stage, -1);
  }

  @Override
  public final void begin(IStage stage, int total) throws InterruptedException {
    checkInterrupted();
    closed = false;
    State state = new State(stack.isEmpty() ? null : stack.peek(), stage, total);
    notifyStage(state, stage.beginString(), false);
    stack.push(state);
  }

  @Override
  public final void step(String message, Object... args) throws InterruptedException {
    checkInterrupted();
    State currentState;
    if (stack.isEmpty() || (currentState = stack.peek()).isAborted())
      return;
    currentState.incrementAndGet();
    notifyStep(currentState, String.format(message, args));
  }

  @Override
  public final void end() throws InterruptedException {
    checkInterrupted();
    if (stack.isEmpty() || stack.peek().isAborted())
      return;
    State state = stack.pop();
    state.end();
    String message = state.getStage().endString() + " em " + state.getTime() + "ms";
    notifyStage(state, message, true);
  }
  
  private void checkInterrupted() throws InterruptedException {
    if (Thread.currentThread().isInterrupted()) {
      throw abort(new InterruptedException(CANCELED_OPERATION_MESSAGE));
    }
  }

  @Override
  public final <T extends Throwable> T abort(T e) {
    State currentState;
    if (stack.isEmpty() || (currentState = stack.peek()).isAborted())
      return e;
    String message = e.getMessage() + ". Causa: " + Throwables.rootString(e); 
    notifyStep(currentState.abort(e), message);
    message = currentState.getStage().endString() + " abortado em " + currentState.getTime() + "ms";
    notifyStage(currentState, message, true);
    return e;
  }
  
  @Override
  public final boolean isClosed() {
    return closed;
  }
  
  @Override
  public final IProgress reset() {
    if (!isClosed()) {
      try {
        this.stack.clear();
        this.complete();
      }finally {
        this.resetObservables();
        this.closed = true;
      }
    }
    return this;
  }

  private void complete() {
    try {
      stepSubject.onComplete();
    }finally {
      try {
        stageSubject.onComplete();
      }finally {
        disposeSubject.onComplete();
      }
    }
  }
  
  private void notifyStep(IState state, String message) {
    this.stepSubject.onNext(new StepEvent(state, message, this.stack.size()));
  }

  private void notifyStage(IState state, String message, boolean end) {
    this.stageSubject.onNext(new StageEvent(state, message, this.stack.size(), end));
  }
  
  private void resetObservables() {
    this.stepSubject = BehaviorSubject.create();
    this.stageSubject = BehaviorSubject.create();
    this.disposeSubject = BehaviorSubject.create();
  }
  
  @Override
  public final Observable<IStepEvent> stepObservable() {
    return this.stepSubject;
  }

  @Override
  public final BehaviorSubject<IStageEvent> stageObservable() {
    return this.stageSubject;
  }

  @Override
  public final BehaviorSubject<IProgress> disposeObservable() {
    return this.disposeSubject;
  }
  
  @Override
  public final IProgress stackTracer(Consumer<IState> consumer) {
    this.stack.forEach(consumer);
    return this;
  }

  @Override
  public final void dispose() {
    this.disposeSubject.onNext(this);
    this.reset();
  }
}
