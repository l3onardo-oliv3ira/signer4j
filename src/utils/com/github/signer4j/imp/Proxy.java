package com.github.signer4j.imp;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public  final class Proxy {

  private static String proxyEndereco = null;
  private static String proxyPorta = null;
  private static String proxyUsuario = null;
  private static String proxySenha = null;

  private Proxy() {
  }

  public static String getProxyEndereco() {
    return proxyEndereco;
  }

  public static void setProxyEndereco(String proxyEndereco) {
    Proxy.proxyEndereco = proxyEndereco;
  }

  public static String getProxyPorta() {
    return proxyPorta;
  }

  public static void setProxyPorta(String proxyPorta) {
    Proxy.proxyPorta = proxyPorta;
  }

  public static String getProxyUsuario() {
    return proxyUsuario;
  }

  public static void setProxyUsuario(String proxyUsuario) {
    Proxy.proxyUsuario = proxyUsuario;
  }

  public static String getProxySenha() {
    return proxySenha;
  }

  public static void setProxySenha(String proxySenha) {
    Proxy.proxySenha = proxySenha;
  }
  
  public static void setSystem() throws Exception {
    System.clearProperty("http.proxyHost");
    System.clearProperty("http.proxyPort");
    System.clearProperty("http.proxyUser");
    System.clearProperty("http.proxyPassword");
    System.clearProperty("https.proxyHost");
    System.clearProperty("https.proxyPort");
    System.clearProperty("https.proxyUser");
    System.clearProperty("https.proxyPassword");
    Authenticator.setDefault(null);
    System.setProperty("java.net.useSystemProxies", "true");  
  }
  
  public static void setProxy()throws Exception {   
    try{
      if(proxyEndereco == null || proxyEndereco.trim().isEmpty() || proxyPorta == null || proxyPorta.trim().isEmpty() ){
        throw new Exception("invalid proxy parameters");
      }

      Authenticator.setDefault(new Authenticator() {
          @Override
          public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(proxyUsuario, proxySenha.toCharArray());
          }
        }
      );
      System.setProperty("http.proxyHost", proxyEndereco);
      System.setProperty("http.proxyPort", proxyPorta);
      System.setProperty("http.proxyUser", proxyUsuario);
      System.setProperty("http.proxyPassword", proxySenha);
      System.setProperty("https.proxyHost", proxyEndereco);
      System.setProperty("https.proxyPort", proxyPorta);
      System.setProperty("https.proxyUser", proxyUsuario);
      System.setProperty("https.proxyPassword", proxySenha);
    }   
    catch (Exception e) {     
      throw new Exception("unabled to set proxy", e);
    }   
  }
}
