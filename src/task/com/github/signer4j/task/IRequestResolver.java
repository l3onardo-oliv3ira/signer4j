package com.github.signer4j.task;

import com.github.signer4j.task.exception.TaskResolverException;

public interface IRequestResolver<I, O, R extends ITaskRequest<O>> {

  R resolve(I request) throws TaskResolverException;
}
