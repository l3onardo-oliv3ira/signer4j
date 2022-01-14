package com.github.signer4j.imp.function;

@FunctionalInterface
public interface Supplier<T> {
  T get() throws Exception;
}

