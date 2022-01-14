package com.github.signe4j.imp.function;

@FunctionalInterface
public interface Procedure<R, E extends Exception> {
  R call() throws E;
}
