package com.github.signer4j.task.imp;

import java.io.IOException;
import java.util.function.Function;

import com.github.signer4j.ITextReader;
import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.JsonTextReader;
import com.github.signer4j.imp.Params;
import com.github.signer4j.task.IRequestReader;
import com.github.signer4j.task.ITask;

public abstract class AbstractRequestReader<P extends Params, Pojo> implements IRequestReader<P>{

  private ITextReader pojoReader;  
  
  public AbstractRequestReader(Class<?> jsonClass) {
    this(new JsonTextReader(jsonClass));
  }
  
  public AbstractRequestReader(ITextReader pojoReader) {
    this.pojoReader = Args.requireNonNull(pojoReader,  "pojoReader is null");    
  }

  @SuppressWarnings("unchecked")
  @Override
  public final P read(String text, P params, Function<?,?> wrapper) throws IOException {    
    ITask<?> task = createTask(params, (Pojo)wrapper.apply(pojoReader.read(text)));
    StringBuilder whyNot = new StringBuilder();
    if (!task.isValid(whyNot)) {
      throw new IOException("Unabled to create a valid task with parameter: " + text + " reason: " + whyNot);
    }
    return params;
  }
  
  protected abstract ITask<?> createTask(P output, Pojo pojo) throws IOException;
}
