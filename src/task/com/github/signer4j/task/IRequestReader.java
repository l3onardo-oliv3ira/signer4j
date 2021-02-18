package com.github.signer4j.task;

import java.io.IOException;

import com.github.signer4j.imp.Params;

public interface IRequestReader<T extends Params> {
  
  T read(String text, T params) throws IOException;
}
