package com.github.signer4j;

public interface IThreadContext {

  public void start();

  public void stop();

  public void stop(long timeout);
}
