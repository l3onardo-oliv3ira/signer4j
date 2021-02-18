package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;

import java.util.Scanner;

import javax.security.auth.callback.PasswordCallback;

import com.github.signer4j.IPasswordCallbackHandler;

public enum ConsoleCallback implements IPasswordCallbackHandler {
  HANDLER;
  
  @Override
  public ResponseCallback doHandle(final PasswordCallback callback) {
    requireNonNull(callback, "Password callback is null");
    System.out.print("Password: ");
    @SuppressWarnings("resource")
    Scanner sc = new Scanner(System.in);
    String password;
    password = sc.nextLine();
    if (Strings.isEmpty(password))
      return ResponseCallback.CANCEL;
    callback.setPassword(password.toCharArray());
    return ResponseCallback.OK;
  }
}
