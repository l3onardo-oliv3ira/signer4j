package com.github.signer4j.imp;

import com.github.signer4j.IThreadContext;

public abstract class ThreadContext implements IThreadContext {

  private Thread context;
  private final String name;
  private final boolean deamon;

  public ThreadContext(String name) {
    this(name, false);
  }

  public ThreadContext(String name, boolean deamon) {
    this.deamon = deamon;
    this.name = name;
  }

  @Override
  public final synchronized void start() {
    stop();
    context = new Thread(name) {
      @Override
      public void run() {
        doRun();
      }
    };
    context.setDaemon(deamon);
    context.start();
  }

  @Override
  public final synchronized void stop(long timeout) {
    if (context != null) {
      context.interrupt();
      try {
        doInterrupt();
        context.join(timeout);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } finally {
        onStoped();
        context = null;
      }
    }
  }

  @Override
  public final void stop() {
    stop(0);
  }

  protected void onStoped() {}

  protected void doInterrupt() {}

  protected abstract void doRun();
}
