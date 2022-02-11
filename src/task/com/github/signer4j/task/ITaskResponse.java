package com.github.signer4j.task;

import java.io.IOException;

public interface ITaskResponse<T> {
  boolean isSuccess();
  void processResponse(T response) throws IOException;
}
