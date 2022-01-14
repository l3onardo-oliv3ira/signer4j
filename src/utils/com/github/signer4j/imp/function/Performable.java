package com.github.signer4j.imp.function;

@FunctionalInterface
public interface Performable<T> {
  void perform(T t);
}
