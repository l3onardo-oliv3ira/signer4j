package com.github.signer4j.progress.imp;

@FunctionalInterface
public interface ICanceller {
  ICanceller NOTHING = (r) -> {};
  
  void cancelCode(Runnable cancelCode);
}