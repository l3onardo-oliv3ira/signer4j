package com.github.signer4j;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface IStreamProvider<T> {
  Stream<T> stream();

  default Stream<T> filter(Predicate<T> filter) {
    return stream().filter(filter);
  }
}
