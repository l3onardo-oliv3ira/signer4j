package com.github.signer4j.task;

import java.io.IOException;

@FunctionalInterface
public interface ITaskResponse<T> {

  void processResponse(T response) throws IOException;
}
