package com.github.signer4j.imp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.imp.function.Executable;
import com.github.signer4j.imp.function.Procedure;

public class Throwables {
  private static final Logger LOGGER = LoggerFactory.getLogger(Throwables.class);
  
  private Throwables() {}
  
  public static boolean tryRun(Executable<?> e) {
    return tryRun(e, false);
  }
  
  public static <E extends Exception> boolean tryRun(Procedure<Boolean, E> procedure) { 
    return tryRun(procedure, false);
  }

  public static boolean tryRun(Executable<?> e, boolean defaultIfFail) {
    return tryRun(e, defaultIfFail, false);
  }

  public static boolean tryRun(Executable<?> e, boolean defaultIfFail, boolean logQuietly) {
    try {
      e.execute();
      return true;
    }catch(Exception ex) {
      if (!logQuietly) {
        LOGGER.warn("tryRun fail", ex);
      }
      return defaultIfFail;
    }
  }
  
  public static <E extends Exception> boolean tryRun(Procedure<Boolean, E> procedure, boolean logQuietly) {
    try {
      return procedure.call();
    }catch(Exception ex) {
      if (!logQuietly) {
        LOGGER.warn("tryRun fail", ex);
      }
      return false;
    }
  }
  
  public static Optional<Exception> tryCatch(Executable<?> e) {
    try {
      e.execute();
      return Optional.empty();
    }catch(Exception ex) {
      return Optional.of(ex);
    }
  }
  
  public static void tryCatch(Executable<?> e, Consumer<Exception> catchBlock) {
    try {
      e.execute();
    }catch(Exception ex) {
      catchBlock.accept(ex);
    }
  }
  
  public static Optional<Exception> tryCatch(Procedure<?, Exception> p) {
    try {
      p.call();
      return Optional.empty();
    }catch(Exception ex) {
      return Optional.of(ex);
    }
  }
  
  public static void tryCatch(Procedure<?, Exception> p, Consumer<Exception> catchBlock) {
    try {
      p.call();
    }catch(Exception ex) {
      catchBlock.accept(ex);
    }
  }
  
  public static Throwable rootCause(Throwable throwable) {
    while(throwable != null){
      Throwable rootCause = throwable.getCause();
      if (rootCause == null || rootCause == throwable)
        break;
      throwable = rootCause;
    }
    return throwable;
  }
  
  public static String rootString(Throwable throwable) {
    Throwable rootCause = rootCause(throwable);
    if (rootCause == null)
      return "Causa desconhecida";
    StringWriter w = new StringWriter();
    try(PrintWriter p = new PrintWriter(w)){
      rootCause.printStackTrace(p);
      return w.toString();
    }
  }

  public static void tryRuntime(Executable<?> e) {
    try {
      e.execute();
    }catch(RuntimeException rte) {
      throw rte;
    }catch(Exception ex) {
      throw new RuntimeException(ex);
    }
  }
  
  public static void tryRuntime(Executable<?> e, String message) {
    try {
      e.execute();
    }catch(Exception ex) {
      throw new RuntimeException(message, ex);
    }
  }

  public static <T, E extends Exception> T tryRuntime(Procedure<T, E> procedure) {
    return tryRuntime(procedure, "");
  }

  public static <T, E extends Exception> T tryRuntime(Procedure<T, E> procedure, String throwMessageIfFail) {
    return tryRuntime(procedure, () -> throwMessageIfFail);
  }
  
  public static <T, E extends Exception> T tryRuntime(Procedure<T, E> procedure, Supplier<String> throwMessageIfFail) {
    return tryRuntime(procedure, (ex) -> new RuntimeException(Strings.needText(throwMessageIfFail.get(), "tryRuntime fail"), ex));
  }
  
  @SuppressWarnings("unchecked")
  public static <T, E extends Exception> T tryRuntime(Procedure<T, E> procedure, Function<E, RuntimeException> wrapper) {
    try {
      return procedure.call();
    }catch(Exception ex) {
      throw wrapper.apply((E)ex);
    }
  }
  
  public static <T, E extends Exception> T tryCall(Procedure<T, E> procedure, T defaultIfFail) {
    return tryCall(procedure, defaultIfFail, false);
  }

  public static <T, E extends Exception> T tryCall(Procedure<T, E> procedure, Supplier<T> defaultIfFail) {
    return tryCall(procedure, defaultIfFail, false);
  }

  public static <T, E extends Exception> T tryCall(Procedure<T, E> procedure, T defaultIfFail, boolean logQuietly) {
    return tryCall(procedure, (Supplier<T>)() -> defaultIfFail, logQuietly);
  }
  
  public static <T, E extends Exception> T tryCall(Procedure<T, E> procedure, Supplier<T> defaultIfFail, boolean logQuietly) {
    try {
      return procedure.call();
    }catch(Exception e) {
      if (!logQuietly) {
        LOGGER.warn("tryCall fail", e);
      }
      return defaultIfFail.get();
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
