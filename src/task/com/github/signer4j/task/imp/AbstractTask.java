package com.github.signer4j.task.imp;

import com.github.signer4j.IParam;
import com.github.signer4j.imp.Params;
import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.imp.ProgressOptions;
import com.github.signer4j.task.ITask;

public abstract class AbstractTask<T> implements ITask<T> {

  protected final Params params;

  protected AbstractTask(Params params) {
    this.params = params.of(ITask.TASK_INSTANCE, this);
  }

  protected final Params getParams() {
    return params;
  }
  
  protected final IParam getParameter(String key) {
    return params.get(key);
  }
  
  protected final <V> V getParameterValue(String key) {
    return params.getValue(key);
  }
  
  protected final IProgress getProgress() {
    return getParameter(IProgress.PROGRESS_PARAM).orElse(ProgressOptions.IDLE);
  }
  
  @Override
  public boolean isValid(StringBuilder whyNot) {
    return true;
  }
}
