package com.github.signer4j;

public interface ICMSSigner extends IByteProcessor {
  ICMSSigner usingAttributes(boolean hasSignedAttributes);
}
