package com.github.signer4j.imp;

import javax.security.auth.callback.PasswordCallback;

import com.github.signer4j.IPasswordCallbackHandler;

public class LiteralPasswordCallbackHandler implements IPasswordCallbackHandler {

  private char[] password;
  
  public LiteralPasswordCallbackHandler(char[] password) {
    this.password = password;
  }

  @Override
  public ResponseCallback doHandle(PasswordCallback callback) {
    callback.setPassword(password);
    return ResponseCallback.OK;
  }
}
