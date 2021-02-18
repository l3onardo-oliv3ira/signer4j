package com.github.signer4j.task.exception;

public class TaskException extends Exception {
  private static final long serialVersionUID = 5036913751425298195L;
  
  public TaskException(String message) {
    super(message);
  }
  
  public TaskException(String message, Throwable cause) {
    super(message, cause);
  }
}
