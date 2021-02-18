package com.github.signer4j.imp;

@FunctionalInterface
public interface Exec<T> {
  void exec(T t);
}
