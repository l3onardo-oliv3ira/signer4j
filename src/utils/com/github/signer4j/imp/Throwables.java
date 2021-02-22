package com.github.signer4j.imp;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.Supplier;

public class Throwables {
  private static final Logger LOGGER = LoggerFactory.getLogger(Throwables.class);
  
  private Throwables() {}
  
  public static boolean tryRun(Executable<?> e) {
    return tryRun(e, false);
  }
  
  public static Optional<Throwable> tryCatch(Executable<?> e) {
    try {
      e.exec();
      return Optional.empty();
    }catch(Throwable ex) {
      return Optional.of(ex);
    }
  }
  
  public static void throwRuntime(Executable<?> e) {
    try {
      e.exec();
    }catch(RuntimeException rte) {
      throw rte;
    }catch(Throwable ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    }
  }
  
  public static void throwRuntime(Executable<?> e, String message) {
    try {
      e.exec();
    }catch(RuntimeException rte) {
      throw rte;
    }catch(Throwable ex) {
      throw new RuntimeException(message, ex);
    }
  }

  public static boolean tryRun(Executable<?> e, boolean quietly) {
    try {
      e.exec();
      return true;
    }catch(Throwable ex) {
      if (!quietly) {
        LOGGER.warn("tryRun exception", ex);
      }
      return false;
    }
  }
  
  public static <E extends Exception> boolean tryRun(Procedure<Boolean, E> procedure) {
    try {
      return procedure.call();
    }catch(Throwable ex) {
      LOGGER.warn("tryRun exception", ex);
      return false;
    }
  }
  
  public static <T, E extends Exception> T tryCall(Procedure<T, E> procedure, T defaultFail) {
    try {
      return procedure.call();
    }catch(Throwable e) {
      LOGGER.warn("tryCall exception", e);
      return defaultFail;
    }
  }
  
  public static <T, E extends Exception> T tryCall(Procedure<T, E> procedure, Supplier<String> throwMessage) {
    try {
      return procedure.call();
    }catch(RuntimeException rte) {
      throw rte;
    }catch(Throwable ex) {
      throw new RuntimeException(throwMessage.get(), ex);
    }
  }

  public static boolean hasCause(Throwable e, Class<?> clazz) {
    if (e == null)
      return false;
    if (clazz.isInstance(e))
      return true;
    Throwable cause;
    if (e == (cause = e.getCause())) //block recursive call infinitly
      return false;
    return hasCause(cause, clazz);
  }
}
