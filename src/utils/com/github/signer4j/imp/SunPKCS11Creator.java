package com.github.signer4j.imp;

import static com.github.signer4j.imp.Throwables.tryRuntime;
import static java.lang.Class.forName;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.AuthProvider;
import java.security.Provider;
import java.security.Security;

class SunPKCS11Creator {
  
  private static final String SUN_PKCS11_PROVIDERNAME = "SunPKCS11";
  
  private static final String SUN_PKCS11_CLASS_NAME = "sun.security.pkcs11.SunPKCS11";
  
  private static final Class<?> SUN_PKCS11_PROVIDER_CLASS = tryRuntime(() -> forName(SUN_PKCS11_CLASS_NAME), SUN_PKCS11_CLASS_NAME + " not found");
  
  private SunPKCS11Creator() {}
  
  public static AuthProvider create(String configString) {
    if (isJavaGreaterOrEquals9())
      return createProviderJavaGreaterOrEquals9(configString);
    return createProviderJavaLowerThan9(configString);
  }
  
  private static boolean isJavaGreaterOrEquals9() {
    try {
      Method configureMethod = SUN_PKCS11_PROVIDER_CLASS.getMethod("configure", String.class);
      return configureMethod != null;
    } catch (NoSuchMethodException e) {
    }
    return false;
  }

  private static AuthProvider createProviderJavaGreaterOrEquals9(String configString) {
    try {
      Provider provider = Security.getProvider(SUN_PKCS11_PROVIDERNAME);
      Method configureMethod = SUN_PKCS11_PROVIDER_CLASS.getMethod("configure", String.class);
      // "--" is permitted in the constructor sun.security.pkcs11.Config
      return (AuthProvider)configureMethod.invoke(provider, "--" + configString);
    } catch (Exception e) {
      throw new RuntimeException("Unable to instantiate PKCS11 (JDK >= 9)", e);
    }
  }
  
  private static AuthProvider createProviderJavaLowerThan9(String configString) {
    try (ByteArrayInputStream bais = new ByteArrayInputStream(configString.getBytes())) {
      return (AuthProvider)SUN_PKCS11_PROVIDER_CLASS.getConstructor(InputStream.class).newInstance(bais);
    } catch (Exception e) {
      throw new RuntimeException("Unable to instantiate PKCS11 (JDK < 9) ", e);
    }
  }
}
