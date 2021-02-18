package com.github.signer4j.imp;

@FunctionalInterface
public interface Executable<T extends Exception> {
  void exec() throws T;
}

