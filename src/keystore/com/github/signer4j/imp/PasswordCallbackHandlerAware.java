package com.github.signer4j.imp;

import javax.security.auth.callback.PasswordCallback;

import com.github.signer4j.IPasswordCallbackHandler;

public class PasswordCallbackHandlerAware implements IPasswordCallbackHandler{

  private boolean wasHandle = false;
  
  private IPasswordCallbackHandler handler;
  
  public boolean isHandled() {
    return wasHandle;
  }
  
  public ResponseCallback doHandle(final PasswordCallback callback) {
    wasHandle = true;
    return handler.doHandle(callback);
  }
}
