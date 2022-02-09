package com.github.signer4j.imp.function;

@FunctionalInterface
public interface Caller<T, R, E extends Exception> {
  R call(T t) throws E;
}

