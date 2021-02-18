package com.github.signer4j.imp;

import java.util.function.Consumer;

public abstract class InvokeHandler<E extends Throwable> {
  
  protected InvokeHandler() {}
  
  public final <T> T invoke(Supplier<T> tryBlock) throws E { 
    return invoke(tryBlock, (e) -> {});
  }

  public final <T> T invoke(Supplier<T> tryBlock, Runnable finallyBlock) throws E {
    return invoke(tryBlock, (t) -> {}, finallyBlock);
  }
    
  public final <T> T invoke(Supplier<T> tryBlock, Consumer<Throwable> catchBlock) throws E {
    return invoke(tryBlock, catchBlock, () -> {});
  }

  public abstract <T> T invoke(Supplier<T> tryBlock, Consumer<Throwable> catchBlock, Runnable finallyBlock) throws E;
}
