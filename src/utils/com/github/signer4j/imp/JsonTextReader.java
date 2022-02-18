package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;

import java.io.IOException;
import java.nio.charset.Charset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.signer4j.ITextReader;

public class JsonTextReader implements ITextReader {

  private final Class<?> clazz;
  private final Charset charset;
  
  public JsonTextReader(Class<?> clazz) {
    this(clazz, Constants.DEFAULT_CHARSET);
  }
  
  public JsonTextReader(Class<?> clazz, Charset charset) {
    this.clazz = requireNonNull(clazz, "clazz is null");
    this.charset = requireNonNull(charset, "charset is null");
  }

  
  @Override
  @SuppressWarnings("unchecked")
  public <T> T read(String text) throws IOException {
    return (T) new ObjectMapper().readValue(text.getBytes(charset), clazz); 
  }
}
