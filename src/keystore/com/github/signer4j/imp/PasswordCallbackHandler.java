package com.github.signer4j.imp;

import javax.security.auth.callback.PasswordCallback;

import com.github.signer4j.IPasswordCallbackHandler;

public enum PasswordCallbackHandler implements IPasswordCallbackHandler {
  NULL;
  
  @Override
  public ResponseCallback doHandle(PasswordCallback callback) {
    callback.setPassword(null);
    return ResponseCallback.OK;
  }
}
