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
  public final void start() {
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
  public final void stop(long timeout) {
    if (context != null) {
      context.interrupt();
      try {
        context.join(timeout);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } finally {
        context = null;
      }
    }
  }

  @Override
  public final void stop() {
    stop(0);
  }

  protected abstract void doRun();
}
