package com.github.signer4j.imp;

@FunctionalInterface
public interface Procedure<R, E extends Exception> {
  R call() throws E;
}
