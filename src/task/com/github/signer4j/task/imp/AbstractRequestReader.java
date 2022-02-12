package com.github.signer4j.task.imp;

import java.io.IOException;

import com.github.signer4j.ITextReader;
import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.JsonTextReader;
import com.github.signer4j.imp.Params;
import com.github.signer4j.task.IRequestReader;
import com.github.signer4j.task.ITask;

public abstract class AbstractRequestReader<P extends Params, O> implements IRequestReader<P>{

  private ITextReader pojoReader;
  
  public AbstractRequestReader(Class<O> jsonClass) {
    this(new JsonTextReader(jsonClass));
  }

  public AbstractRequestReader(ITextReader pojoReader) {
    this.pojoReader = Args.requireNonNull(pojoReader,  "pojoReader is null");
  }

  @Override
  public final P read(String text, P params) throws IOException {
    ITask<?> task = createTask(params, pojoReader.read(text));
    StringBuilder whyNot = new StringBuilder();
    if (!task.isValid(whyNot)) {
      throw new IOException("Unabled to create a valid task with parameter: " + text + " reason: " + whyNot);
    }
    return params;
  }
  
  protected abstract ITask<?> createTask(P output, O pojo) throws IOException;
}
