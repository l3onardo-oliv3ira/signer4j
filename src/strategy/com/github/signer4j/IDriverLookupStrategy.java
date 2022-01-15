package com.github.signer4j;

public interface IDriverLookupStrategy {
  IDriverLookupStrategy IDLE = (v) -> {};

  void lookup(IDriverVisitor visitor);
}
