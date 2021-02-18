package com.github.signer4j.task.exception;

import java.io.IOException;

public class TaskResponseException extends Exception {

  private static final long serialVersionUID = 1L;
  
  public TaskResponseException(String message, IOException e) {
    super(message, e);
  }
}
