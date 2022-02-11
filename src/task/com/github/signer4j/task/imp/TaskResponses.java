package com.github.signer4j.task.imp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.signer4j.task.ITaskResponse;

public class TaskResponses<T> implements ITaskResponse<T> {
  
  private List<ITaskResponse<T>> responses = new ArrayList<>(4);

  @Override
  public boolean isSuccess() {
    return true;
  }

  public TaskResponses<T> add(ITaskResponse<T> response) {
    if (response != null) {
      responses.add(response);
    }
    return this;
  }


  @Override
  public void processResponse(T response) throws IOException {
    for(ITaskResponse<T> r: responses) {
      r.processResponse(response);
    }
  }
}
