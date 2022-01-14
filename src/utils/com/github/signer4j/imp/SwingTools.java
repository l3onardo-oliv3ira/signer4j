package com.github.signer4j.imp;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signe4j.imp.function.Supplier;

public class SwingTools {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(SwingTools.class);
      
  private SwingTools() {}
  
  public static void invokeLater(Runnable r) {
    SwingUtilities.invokeLater(r);
  }
  
  public static void invokeAndWait(Runnable r) {
    try {
      SwingUtilities.invokeAndWait(r);
    } catch (InvocationTargetException | InterruptedException e) {
      LOGGER.warn("Exceção em thread swing", e);
    }
  }

  public static boolean isTrue(Supplier<Boolean> supplier){
    return invoke(supplier).orElse(Boolean.FALSE);
  }

  
  public static <T> Optional<T> invoke(Supplier<T> supplier){
    AtomicReference<T> ref = new AtomicReference<>();
    try {
      invokeAndWait(() -> {
        try {
          ref.set(supplier.get());
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      });
    } catch (Exception e) {
      LOGGER.warn("Exceção em thread swing", e);
    }
    return Optional.ofNullable(ref.get());
  }
}
