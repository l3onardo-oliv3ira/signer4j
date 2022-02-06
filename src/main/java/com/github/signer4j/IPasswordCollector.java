package com.github.signer4j;

public interface IPasswordCollector {
  IPasswordCollector NOTHING = p -> {};
  
  void collect(char[] password);
}
