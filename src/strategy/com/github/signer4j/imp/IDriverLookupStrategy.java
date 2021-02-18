package com.github.signer4j.imp;

public interface IDriverLookupStrategy {
  IDriverLookupStrategy IDLE = (v) -> {};

  void lookup(IDriverVisitor visitor);
}
