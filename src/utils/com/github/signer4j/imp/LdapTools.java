package com.github.signer4j.imp;

public abstract class LdapTools {
  private LdapTools() {}
  
  public static String shortAccount(String account) {
    String p = account;
    int idx = p.indexOf('@');
    if (idx > 0)
      p = p.substring(0, idx);
    return p;
  }
  
  public static String fullAccount(String account, String domain) {
    int idx = account.indexOf('@'); 
    if (idx >= 0)
      return account;
    return account + domain;
  }
}
