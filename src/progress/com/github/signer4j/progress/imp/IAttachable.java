package com.github.signer4j.progress.imp;

@FunctionalInterface
public interface IAttachable {
  void attach(Runnable cancelCode);
}