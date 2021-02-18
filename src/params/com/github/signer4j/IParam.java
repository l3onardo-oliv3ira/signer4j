package com.github.signer4j;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IParam {
  
  String getName();
  
  boolean isPresent();

  <T> T get();
  
  <T> T orElse(T other);
  
  <T> T orElseGet(Supplier<? extends T> other);
  
  <T> void ifPresent(Consumer<T> consumer);
  
  <X extends Throwable, T> T orElseThrow(Supplier<? extends X> supplier) throws X;
}