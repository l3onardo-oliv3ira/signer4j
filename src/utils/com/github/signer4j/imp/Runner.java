package com.github.signer4j.imp;

@FunctionalInterface
public interface Runner<T, E extends Exception> {
  void exec(T T) throws E;
}
