package com.github.signer4j.imp;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Containers {
  private Containers() {
  }

  public static boolean isEmpty(Collection<?> c) {
    return c == null || c.size() == 0;
  }

  public static boolean isEmpty(Map<?, ?> m) {
    return m == null || m.isEmpty();
  }

  public static boolean isEmpty(Set<?> set) {
    return set == null || set.isEmpty();
  }

  public static <T> List<T> toList(Set<T> set) {
    return isEmpty(set) ? emptyList() : new ArrayList<>(set);
  }
  
  public static <T> List<T> toUnmodifiableList(Set<T> set) {
    return isEmpty(set) ? emptyList() : unmodifiableList(new ArrayList<>(set));
  }

  public static String firstText(Collection<String> values) {
    if (values == null || values.isEmpty())
      return "";
    return Strings.text(values.iterator().next());
  }

  public static boolean isEmpty(Enumeration<?> e) {
    return e == null || !e.hasMoreElements();
  }

  public static boolean isEmpty(Object[] value) {
    return value == null || value.length == 0;    
  }

  public static boolean isEmpty(Number[] value) {
    return value == null || value.length == 0;    
  }

  public static boolean isEmpty(byte[] value) {
    return value == null || value.length == 0;    
  }
}
