package com.github.signe4j.imp.function;

@FunctionalInterface
public interface Performable<T> {
  void perform(T t);
}
