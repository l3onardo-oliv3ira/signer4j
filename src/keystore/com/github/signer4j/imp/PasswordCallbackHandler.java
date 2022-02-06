package com.github.signer4j.imp;

import java.util.Scanner;

import javax.security.auth.callback.PasswordCallback;

import com.github.signer4j.IPasswordCallbackHandler;

public enum PasswordCallbackHandler implements IPasswordCallbackHandler {
  NULL() {
    @Override
    public ResponseCallback doHandle(PasswordCallback callback) {
      Args.requireNonNull(callback, "Password callback is null");
      callback.setPassword(null);
      return ResponseCallback.OK;
    }
  },
  CONSOLE(){
    @Override
    public ResponseCallback doHandle(final PasswordCallback callback) {
      Args.requireNonNull(callback, "Password callback is null");
      System.out.print("Password: ");
      @SuppressWarnings("resource")
      Scanner sc = new Scanner(System.in);
      String password = sc.nextLine();
      if (Strings.isEmpty(password))
        return ResponseCallback.CANCEL;
      callback.setPassword(password.toCharArray());
      return ResponseCallback.OK;
    }
  };
}
