package com.github.signer4j.imp;

import com.github.signer4j.imp.exception.InterruptedSigner4JRuntimeException;
import com.github.utils4j.imp.BooleanTimeout;

//please do NOT public this class!
final class Signer4jContext {
  
  private static final BooleanTimeout ESCAPE_DISCARDING = new BooleanTimeout("signer4j-context");

  static long timeout() {
    return ESCAPE_DISCARDING.getTimeout();
  }
  
  static boolean isDiscarded() {
    return ESCAPE_DISCARDING.isTrue();
  }
  
  static void checkDiscarded() {
    checkDiscarded(false);
  }
  
  static void discard() {
    ESCAPE_DISCARDING.setTrue();    
    throw new InterruptedSigner4JRuntimeException();
  }

  static void discard(Throwable e) {
    ESCAPE_DISCARDING.setTrue();
    throw new InterruptedSigner4JRuntimeException(e);
  }
  
  static void discardQuietly() {
    ESCAPE_DISCARDING.setTrue();
  }

  static void discardQuietly(Runnable runnable) {
    ESCAPE_DISCARDING.setTrue(runnable);
  }

  static void checkDiscarded(boolean checkInterrupted) {
    if (checkInterrupted && Thread.currentThread().isInterrupted())
      discard();
      
    if (isDiscarded()) {
      throw new InterruptedSigner4JRuntimeException();
    }
  }

  private Signer4jContext() {}
}
