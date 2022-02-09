package com.github.signer4j.imp;

import java.util.AbstractMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Pair<K, V> extends AbstractMap.SimpleEntry<K,V>{

  private static final long serialVersionUID = 1L;

  public static <K, V> Pair<K, V> of(K key, V value) {
    return new Pair<K, V>(key, value);
  }
  
  public static <K, V> Pair<K, V> of(K k, Supplier<V> v) {
    Args.requireNonNull(v, "key supplier is null");
    return of(k, v.get());
  }

  public static <K, V> Pair<K, V> of(Supplier<K> k, V v) {
    Args.requireNonNull(k, "key supplier is null");
    return of(k.get(), v);
  }

  public static <K, V> Pair<K, V> of(Supplier<K> k, Supplier<V> v) {
    Args.requireNonNull(k, "key supplier is null");
    Args.requireNonNull(v, "value supplier is null");
    return of(k.get(), v.get());
  }

  private Pair(K key, V value) {
    super(key, value);
  }
  
  public Stream<V> valueStream() {
    return Stream.of(getValue());
  }
  
  public Stream<K> keyStream() {
    return Stream.of(getKey());
  }
}
