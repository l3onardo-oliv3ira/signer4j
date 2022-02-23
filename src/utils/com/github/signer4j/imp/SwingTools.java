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
  
  public static boolean isTrue(Supplier<Boolean> supplier){
    return invokeAndWait(supplier).orElse(Boolean.FALSE);
  }
  
  public static boolean invokeAndWait(Runnable code) {
    return invokeAndWait(code, false);
  }

  public static boolean invokeAndWait(Runnable code, boolean defaultIfFail) {
    Args.requireNonNull(code, "code is null");    
    Procedure<Boolean, ?> p = SwingUtilities.isEventDispatchThread() ? 
      () -> { code.run(); return true; } : 
      () -> { SwingUtilities.invokeAndWait(code); return true;};
    return tryCall(p, defaultIfFail);
  }

  public static void invokeLater(Runnable code) {
    Args.requireNonNull(code, "code is null");
    if (SwingUtilities.isEventDispatchThread()) {
      code.run();
    } else {
      SwingUtilities.invokeLater(code);
    }
  }

  public static <T> Optional<T> invokeAndWait(Supplier<T> supplier){
    Args.requireNonNull(supplier, "supplier is null");
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
