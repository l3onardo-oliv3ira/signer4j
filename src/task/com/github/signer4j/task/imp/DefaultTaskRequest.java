package com.github.signer4j.task.imp;

import com.github.signer4j.imp.Params;
import com.github.signer4j.progress.IProgress;
import com.github.signer4j.task.ITask;
import com.github.signer4j.task.ITaskRequest;

public class DefaultTaskRequest<T> extends Params implements ITaskRequest<T> {
  
  @Override
  public final ITask<T> getTask(IProgress progress) {
    return setProgress(progress).getValue(ITask.TASK_INSTANCE);
  }
  
  private Params setProgress(IProgress progress) {
    return of(IProgress.PROGRESS_PARAM, progress);
  }

  @Override
  public final boolean isValid(StringBuilder invalidCause) {
    return true;
  }
}
