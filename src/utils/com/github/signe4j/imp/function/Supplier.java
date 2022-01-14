package com.github.signe4j.imp.function;

@FunctionalInterface
public interface Supplier<T> {
  T get() throws Exception;
}

