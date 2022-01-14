package com.github.signer4j.imp.function;

@FunctionalInterface
public interface BiProcedure<T1, T2> {
  void call(T1 a, T2 b);
}

