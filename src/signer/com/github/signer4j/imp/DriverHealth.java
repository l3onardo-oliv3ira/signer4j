package com.github.signer4j.imp;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import com.github.signer4j.gui.alert.DriverHealthAlert;
import com.github.utils4j.IDisposable;
import com.github.utils4j.imp.DaemonThreadFactory;
import com.github.utils4j.imp.Services;
import com.github.utils4j.imp.States;

public enum DriverHealth implements IDisposable {
  CHECKER;
  
  private final AtomicBoolean disposed = new AtomicBoolean(false);
  
  private final ExecutorService waiter = Executors.newCachedThreadPool(new DaemonThreadFactory("driver health monitor"));

  final <T> T check(Supplier<T> task, Duration timeout) {
    States.requireFalse(disposed.get(), "Driver health already disposed");
    
    try {
      return waiter.submit(task::get).get(timeout.getSeconds(), TimeUnit.SECONDS);

    } catch (InterruptedException | ExecutionException e) {
      Signer4jContext.discard(e);
    
    } catch (TimeoutException e) {
      DriverHealthAlert.showInfo(
        "O driver do seu dispositivo (token/smartcard) não responde.\n" + 
        "Revise a instalação do mesmo e/ou reinicie o seu computador!"
      );
      Signer4jContext.discard(e);
    }
    
    return null; //This is unreachable code!
  }

  @Override
  public void dispose() {
    if (!disposed.getAndSet(true)) {
      Services.shutdownNow(waiter, 3, 1);
    }
  }
}
