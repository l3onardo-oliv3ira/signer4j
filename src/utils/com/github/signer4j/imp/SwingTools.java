package com.github.signer4j.imp;

import static com.github.signer4j.imp.Throwables.tryCall;
import static com.github.signer4j.imp.Throwables.tryRuntime;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import com.github.signer4j.imp.function.Procedure;
import com.github.signer4j.imp.function.Supplier;

public class SwingTools {
  
  private SwingTools() {}
  
  public static void invokeLater(Runnable r) {
    SwingUtilities.invokeLater(r);
  }
  
  public static boolean isTrue(Supplier<Boolean> supplier){
    return invokeAndWait(supplier).orElse(Boolean.FALSE);
  }
  
  public static boolean invokeAndWait(Runnable r) {
    return invokeAndWait(r, false);
  }

  public static boolean invokeAndWait(Runnable r, boolean defaultIfFail) {
    Procedure<Boolean, ?> p = SwingUtilities.isEventDispatchThread() ? 
        () -> { r.run(); return true; } : 
        () -> { SwingUtilities.invokeAndWait(r); return true;};
    return tryCall(p, defaultIfFail);
  }

  public static <T> Optional<T> invokeAndWait(Supplier<T> supplier){
    Procedure<Optional<T>, ?> p = SwingUtilities.isEventDispatchThread() ? 
      () -> ofNullable(supplier.get()) : 
      () -> {
        final AtomicReference<T> ref = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> tryRuntime(() -> ref.set(supplier.get())));
        return ofNullable(ref.get());
      };
    return tryCall(p, empty());
  }
}
