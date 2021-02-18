package com.github.signer4j;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;

import com.github.signer4j.imp.CanceledOperationException;
import com.github.signer4j.imp.ResponseCallback;

public interface IPasswordCallbackHandler extends CallbackHandler {

  default void handle(final Callback callback) throws IOException {
    handle(new Callback[] { callback });
  }
  
  default void handle(final Callback[] callbacks) throws IOException {
    for (final Callback callback : callbacks) {
      if (callback instanceof PasswordCallback) {
        PasswordCallback pc = ((PasswordCallback) callback);
        pc.clearPassword();
        if (ResponseCallback.CANCEL.equals(doHandle(pc))) {
          throw new CanceledOperationException();
        }
        return;
      }
    }
    throw new CanceledOperationException();
  }
  
  ResponseCallback doHandle(final PasswordCallback callback);
}
