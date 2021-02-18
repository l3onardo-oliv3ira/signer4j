package com.github.signer4j;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public interface ICertificates extends Iterable<ICertificate> {
  IToken getToken();

  int size();
  
  List<ICertificate> toList();

  default Stream<ICertificate> stream() {
    return toList().stream();
  }
  
  default boolean isEmpty() {
    return size() == 0;
  }
  
  default Iterator<ICertificate> iterator() {
    return toList().iterator();
  }
}
