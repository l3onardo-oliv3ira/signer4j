package com.github.signer4j.progress.imp;

import java.util.function.Consumer;

import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.Ids;
import com.github.signer4j.imp.Stack;
import com.github.signer4j.imp.Threads;
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
  public final void begin(IStage stage) {
    begin(stage, -1);
  }

  @Override
  public final void begin(IStage stage, int total) {
    checkInterrupted();
    closed = false;
    stack.push(new State(stack.isEmpty() ? null : stack.peek(), stage, total));
    stageSubject.onNext(new StageEvent(stage, stack.size() - 1, stage.beginString()));
  }

  @Override
  public final void step(String message, Object... args) {
    checkInterrupted();
    State currentState;
    if (stack.isEmpty() || (currentState = stack.peek()).isAborted())
      return;
    currentState.incrementAndGet();
    notify(currentState, String.format(message, args), stack.size());
  }

  @Override
  public final void end() {
    checkInterrupted();
    if (stack.isEmpty() || stack.peek().isAborted())
      return;
    State state = stack.pop();
    state.end();
    String message = state.getStage().endString() + " em " + state.getTime() + "ms";
    stageSubject.onNext(new StageEvent(state.getStage(), stack.size() + 1, message));
  }

  private void checkInterrupted() {
    if (Thread.currentThread().isInterrupted()) {
      ProgressException ex = new ProgressException("Execução cancelada pelo usuário");
      abort(ex);
      throw ex;
    }
  }

  @Override
  public final void abort(Exception e) {
    State currentState;
    if (stack.isEmpty() || (currentState = stack.peek()).isAborted())
      return;
    String message = e.getMessage() + ". Causa: " + Throwables.rootString(e); 
    notify(currentState.abort(e), message, stack.size());
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
  
  private  void notify(final IState state, final String message, int stackSize) {
    this.stepSubject.onNext(new StepEvent(state, message, stackSize));
  }

  private void resetObservables() {
    stepSubject = BehaviorSubject.create();
    stageSubject = BehaviorSubject.create();
    disposeSubject = BehaviorSubject.create();
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
