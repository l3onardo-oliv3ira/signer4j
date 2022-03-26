/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package com.github.signer4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.IConstants;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.OpenByteArrayOutputStream;

public interface IByteProcessor {
  
  ISignedData process(byte[] content, int offset, int length) throws Signer4JException;
  
  default ISignedData process(byte[] content) throws Signer4JException {
    return process(content, 0, content.length);
  }
  
  default ISignedData process(File content) throws Signer4JException, IOException {
    Args.requireNonNull(content, "content is null");
    try(OpenByteArrayOutputStream out = new OpenByteArrayOutputStream(content.length())) {
      Files.copy(content.toPath(), out);
      return out.process(this::process);
    }
  }
  
  default ISignedData process(String content) throws Signer4JException {
    return process(content, IConstants.DEFAULT_CHARSET);
  }
  
  default ISignedData process(String content, Charset charset) throws Signer4JException {
    content = Args.requireText(content, "content is null or empty");
    charset = Args.requireNonNull(charset, "charset is null");
    return process(content.getBytes(charset));
  }
  
  default byte[] processRaw(byte[] content) throws Signer4JException {
    return process(content).getSignature();
  }

  default byte[] processRaw(String content) throws Signer4JException {
    return processRaw(content, IConstants.DEFAULT_CHARSET);
  }
  
  default byte[] processRaw(String content, Charset charset) throws Signer4JException {
    return process(content, charset).getSignature();
  }
  
  default String process64(byte[] content) throws Signer4JException {
    return process(content).getSignature64();
  }

  default String process64(String content) throws Signer4JException {
    return process64(content, IConstants.DEFAULT_CHARSET);
  }
  
  default String process64(String content, Charset charset) throws Signer4JException {
    return process(content, charset).getSignature64();
  }
  
  default String process64(File input) throws Signer4JException, IOException {
    return process(input).getSignature64();
  }
  
  default IByteProcessor config(Object param) {
    return this;
  }
}

