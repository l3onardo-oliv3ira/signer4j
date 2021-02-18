package com.github.signer4j.task;

import com.github.signer4j.progress.IProgress;

public interface ITaskRequest<O> {
  
  ITask<O> getTask(IProgress progress);
  
  boolean isValid(StringBuilder because);
}
