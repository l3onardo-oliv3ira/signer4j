package com.github.signer4j.imp;

import java.util.function.Function;
import java.util.function.Predicate;

@FunctionalInterface
public interface Caller<T, R, E extends Exception> {
  
  @SuppressWarnings("unchecked")
  public static <T, O extends Throwable> T call(Supplier<T> caller, Class<O> clazz) throws O {
    return rethrowIf(caller, t -> clazz.isInstance(t.getCause()), t -> (O)t.getCause());
  }
  
  public static <T, O extends Throwable> T rethrowIf(Supplier<T> caller, Predicate<Throwable> predicate,  Function<Throwable, O> thrower) throws O {
    try {
      return caller.get();
    }catch(Error e) {
      throw e;
    }catch(Throwable e) {
      if (predicate.test(e))
        throw thrower.apply(e);
      if (e instanceof RuntimeException)
        throw (RuntimeException)e;
      throw new RuntimeException(e);
    }
  }

  public static <T, R, E extends Exception> Function<T, R> wrap(Caller<T, R, E> fe) {
    return arg -> {
      try {
        return fe.call(arg);
      } catch(RuntimeException | Error e) {
        throw e;
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }

  R call(T t) throws E;
}

