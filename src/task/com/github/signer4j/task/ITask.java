package com.github.signer4j.task;

import java.util.function.Supplier;

public interface ITask<T> extends Supplier<ITaskResponse<T>> {
  
  String PARAM_NAME = ITask.class.getSimpleName() + ".instance";

  String getId();

  boolean isValid(StringBuilder reasonIfNot);

  default void dispose() {}
}
