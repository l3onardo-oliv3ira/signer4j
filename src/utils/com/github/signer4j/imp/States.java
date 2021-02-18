package com.github.signer4j.imp;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.cert.Certificate;
import java.util.Collection;

public class States {
  private States() {
  }

  public static <T> T requireNonNull(T o, String message) {
    if (o == null)
      throw new IllegalStateException(message);
    return o;
  }

  public static int requirePositive(int value, String message) {
    if (value <= 0)
      throw new IllegalStateException(message);
    return value;
  }

  public static long requirePositive(long value, String message) {
    if (value <= 0)
      throw new IllegalStateException(message);
    return value;
  }

  public static float requirePositive(float value, String message) {
    if (value <= 0)
      throw new IllegalStateException(message);
    return value;
  }

  public static double requirePositive(double value, String message) {
    if (value <= 0)
      throw new IllegalStateException(message);
    return value;
  }

  public static long requireZeroPositive(long value, String message) {
    if (value < 0)
      throw new IllegalStateException(message);
    return value;
  }

  public static String requireText(Object value, String message) {
    String text;
    if (value == null || !Strings.hasText(text = value.toString()))
      throw new IllegalStateException(message);
    return text;
  }
  
  public static String requireText(String value, String message) {
    if (!Strings.hasText(value))
      throw new IllegalStateException(message);
    return value;
  }

  public static void requireText(String[] roots, String message) {
    requireNonNull(roots, message);
    requirePositive(roots.length, message);
    for (String root : roots)
      requireText(root, message);
  }

  public static int requireZeroPositive(int value, String message) {
    if (value < 0)
      throw new IllegalStateException(message);
    return value;
  }

  public static int requireNegative(int value, String message) {
    if (value >= 0)
      throw new IllegalStateException(message);
    return value;
  }

  public static int requireZeroNegative(int value, String message) {
    if (value > 0)
      throw new IllegalStateException(message);
    return value;
  }

  public static boolean requireTrue(boolean value, String message) {
    if (!value)
      throw new IllegalStateException(message);
    return value;
  }

  public static Path requireExists(Path path, String message) {
    return requireExists(path, message, LinkOption.NOFOLLOW_LINKS);
  }
  
  public static Path requireExists(Path path, String message, LinkOption options) {
    if (!Files.exists(requireNonNull(path, message), options))
      throw new IllegalStateException(message);
    return path;
  }
  
  public static <T extends Collection<?>> T requireEmpty(T collection, String message) {
    if (!Containers.isEmpty(collection))
      throw new IllegalStateException(message);
    return collection;
  }

  public static <T extends Collection<?>> T requireNonEmpty(T collection, String message) {
    if (Containers.isEmpty(collection))
      throw new IllegalStateException(message);
    return collection;
  }
  
  public static byte[] requireNonEmpty(byte[] collection, String message) {
    if (Containers.isEmpty(collection))
      throw new IllegalStateException(message);
    return collection;
  }

  public static Certificate[] requireNonEmpty(Certificate[] collection, String message) {
    if (Containers.isEmpty(collection))
      throw new IllegalStateException(message);
    return collection;
  }
  
}
