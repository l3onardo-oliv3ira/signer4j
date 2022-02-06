package com.github.signer4j.task;

import com.github.signer4j.task.exception.TaskExecutorException;

public interface ITaskRequestExecutor<I, O> {
  
  void execute(I request, O response) throws TaskExecutorException;
  void close();
  void setAllowLocalRequest(boolean enabled);
}
