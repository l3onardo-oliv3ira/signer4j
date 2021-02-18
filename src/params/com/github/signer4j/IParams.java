package com.github.signer4j;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IParams {
  IParam get(String key);
  
  default boolean isPresent(String key) {
    return get(key).isPresent();
  }

  default <T> T getValue(String key) {
    return get(key).get();
  }
  
  default <T> T orElse(String key, T value) {
    return get(key).orElse(value);
  }
  
  default <T> T orElseGet(String key, Supplier<? extends T> other) {
    return get(key).orElseGet(other);
  }
  
  default <T> void ifPresent(String key, Consumer<T> consumer) {
    get(key).ifPresent(consumer);
  }
  
  default <X extends Throwable, T> T orElseThrow(String key, Supplier<? extends X> supplier) throws X {
    return get(key).orElseThrow(supplier);
  }
}
