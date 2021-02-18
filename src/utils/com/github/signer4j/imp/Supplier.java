package com.github.signer4j.imp;

@FunctionalInterface
public interface Supplier<T> {
  T get() throws Exception;
}

