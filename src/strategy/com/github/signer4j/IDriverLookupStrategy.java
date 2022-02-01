package com.github.signer4j;

public interface IDriverLookupStrategy {
  IDriverLookupStrategy NOTHING = (v) -> {};

  void lookup(IDriverVisitor visitor);
}
