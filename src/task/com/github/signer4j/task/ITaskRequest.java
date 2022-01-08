package com.github.signer4j.task;

import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.IProgressFactory;

public interface ITaskRequest<O> {
  
  ITask<O> getTask(IProgress progress, IProgressFactory factory);
  
  boolean isValid(StringBuilder because);
}
