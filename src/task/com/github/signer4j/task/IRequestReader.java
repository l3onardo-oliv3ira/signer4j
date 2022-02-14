package com.github.signer4j.task;

import java.io.IOException;
import java.util.function.Function;

import com.github.signer4j.imp.Params;

public interface IRequestReader<T extends Params> {  
  
  default T read(String text, T params) throws IOException {
    return read(text, params, o -> o);
  }
  
  T read(String text, T params, Function<?, ?> wrapper) throws IOException;
}
