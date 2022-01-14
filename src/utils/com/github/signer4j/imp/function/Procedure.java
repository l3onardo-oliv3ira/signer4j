package com.github.signer4j.imp.function;

@FunctionalInterface
public interface Procedure<R, E extends Exception> {
  R call() throws E;
}
