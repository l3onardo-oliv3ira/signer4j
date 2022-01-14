package com.github.signe4j.imp.function;

@FunctionalInterface
public interface Runnable<T, E extends Exception> {
  void run(T T) throws E;
}
