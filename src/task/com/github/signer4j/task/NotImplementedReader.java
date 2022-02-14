package com.github.signer4j.task;

import java.io.IOException;
import java.util.function.Function;

import com.github.signer4j.imp.Params;

public enum NotImplementedReader implements IRequestReader<Params>{
  INSTANCE;
  
  @Override
  public Params read(String text, Params output, Function<?, ?> wrapper) throws IOException {
    throw new IOException("Unrecognizable text format " + text);
  }
}
