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

  /** TODO
   * We have to go back here to analyze if ObjectMapper can be used as attribute of JsonTextReader!
   * Can i use ObjectMapper as a singleton, is it ThreadSafe? If it is, we have some issue with performance?
   */
  @Override
  @SuppressWarnings("unchecked")
  public <T> T read(String text) throws IOException {
    return (T) new ObjectMapper().readValue(text.getBytes(charset), clazz); 
  }
}
