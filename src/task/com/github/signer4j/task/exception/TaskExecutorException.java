package com.github.signer4j.task.exception;

public class TaskExecutorException extends Exception {
  private static final long serialVersionUID = 1L;

  public TaskExecutorException(String message, Throwable e) {
    super(message, e);
  }
}
