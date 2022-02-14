package com.github.signer4j.imp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import com.github.signer4j.InputStreamFactory;

public class OpenByteArrayOutputStream extends ByteArrayOutputStream implements InputStreamFactory {
  
  public static interface IBufferProcessor<T, E extends Throwable> {
    T process(byte[] buffer, int offset, int length) throws E;
  }
  
  public OpenByteArrayOutputStream() {
  }

  public OpenByteArrayOutputStream(long size) {
    super(size > Integer.MAX_VALUE ? 32 : (int)size);
  }

  public final synchronized String asString() {
    return new String(super.buf, 0, super.size());
  }

  public final synchronized String asString(String charset) {
    return asString(Charset.forName(charset));
  }

  public final synchronized String asString(Charset charset) {
    return new String(super.buf, 0, super.size(), charset);
  }
  
  @Override
  public final synchronized InputStream toInputStream() { //don't invoke
    return new ByteArrayInputStream(super.buf, 0, super.size());
  }
  
  public final synchronized <T, E extends Throwable> T process(IBufferProcessor<T, E> processor) throws E {
    return processor.process(super.buf, 0, super.size());
  }
  
  @Override
  public final synchronized void close() {
    this.buf = new byte[32];
    this.count = 0;
  }
}