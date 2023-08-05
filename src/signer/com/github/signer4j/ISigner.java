package com.github.signer4j;

public interface ISigner {
  public ISignedData sign(byte[] content, int offset, int length) throws Exception;
}