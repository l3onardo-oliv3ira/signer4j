package com.github.signer4j.task.imp;

import com.github.signer4j.imp.Params;
import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.IProgressFactory;
import com.github.signer4j.task.ITask;
import com.github.signer4j.task.ITaskRequest;

public class DefaultTaskRequest<T> extends Params implements ITaskRequest<T> {
  
  @Override
  public final ITask<T> getTask(IProgress progress, IProgressFactory factory) {
    return super
      .of(IProgress.PARAM_NAME, progress)
      .of(IProgressFactory.PARAM_NAME, factory)
      .getValue(ITask.PARAM_NAME);
  }
  
  @Override
  public final boolean isValid(StringBuilder invalidCause) {
    return true;
  }
}
