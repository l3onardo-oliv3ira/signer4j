package com.github.signer4j.imp;

@FunctionalInterface
public interface BiProcedure<T1, T2> {
  void execute(T1 a, T2 b);
}

