package com.github.signer4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.Constants;
import com.github.signer4j.imp.OpenByteArrayOutputStream;
import com.github.signer4j.imp.exception.KeyStoreAccessException;

public interface IByteProcessor {
  
  ISignedData process(byte[] content, int offset, int length) throws KeyStoreAccessException;
  
  default ISignedData process(byte[] content) throws KeyStoreAccessException {
    return process(content, 0, content.length);
  }
  
  default ISignedData process(File content) throws KeyStoreAccessException, IOException {
    Args.requireNonNull(content, "content is null");
    try(OpenByteArrayOutputStream out = new OpenByteArrayOutputStream()) {
      Files.copy(content.toPath(), out);
      return out.process(this::process);
    }
  }
  
  default ISignedData process(String content) throws KeyStoreAccessException {
    return process(content, Constants.DEFAULT_CHARSET);
  }
  
  default ISignedData process(String content, Charset charset) throws KeyStoreAccessException {
    content = Args.requireText(content, "content is null or empty");
    charset = Args.requireNonNull(charset, "charset is null");
    return process(content.getBytes(charset));
  }
  
  default byte[] processRaw(byte[] content) throws KeyStoreAccessException {
    return process(content).getSignature();
  }

  default byte[] processRaw(String content) throws KeyStoreAccessException {
    return processRaw(content, Constants.DEFAULT_CHARSET);
  }
  
  default byte[] processRaw(String content, Charset charset) throws KeyStoreAccessException {
    return process(content, charset).getSignature();
  }
  
  default String process64(byte[] content) throws KeyStoreAccessException {
    return process(content).getSignature64();
  }

  default String process64(String content) throws KeyStoreAccessException {
    return process64(content, Constants.DEFAULT_CHARSET);
  }
  
  default String process64(String content, Charset charset) throws KeyStoreAccessException {
    return process(content, charset).getSignature64();
  }
  
  default String process64(File input) throws KeyStoreAccessException, IOException {
    return process(input).getSignature64();
  }
}

