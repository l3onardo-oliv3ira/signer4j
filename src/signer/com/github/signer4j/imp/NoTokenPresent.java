package com.github.signer4j.imp;

import com.github.signer4j.gui.alert.NoTokenPresentInfo;
import com.github.signer4j.imp.exception.NoTokenPresentException;
import com.github.utils4j.imp.function.IProcedure;

public enum NoTokenPresent {
  HANDLER;
  
  public <T> T handle(IProcedure<T, Exception> code, Object lock) {
    synchronized(lock) {

      Signer4jContext.checkDiscarded();
  
      try {
        return code.call();
      
      } catch (NoTokenPresentException e) {
        NoTokenPresentInfo.showInfoOnly();
        Signer4jContext.discard(e);
      
      } catch(Throwable e) {
        Signer4jContext.discard(e);
      }
      
      return null; //unreachabled code because discard will throw exception! (to silence the compiler fail)
    }
  }
}

