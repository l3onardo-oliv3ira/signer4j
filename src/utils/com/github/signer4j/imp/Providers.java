package com.github.signer4j.imp;

import static com.github.signer4j.imp.Throwables.tryRun;
import static java.security.Security.addProvider;
import static java.security.Security.getProvider;
import static java.security.Security.removeProvider;
import static java.util.Optional.ofNullable;
import static org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;

import java.io.InputStream;
import java.security.AuthProvider;
import java.security.Provider;
import java.security.Security;
import java.util.Optional;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jcp.xml.dsig.internal.dom.XMLDSigRI;

public class Providers {
  
  private static final String JSR_105_PROVIDER = "XMLDSig"; //org.jcp.xml.dsig.internal.dom.XMLDSigRI
  
  private Providers() {}
  
  public static Provider installBouncyCastleProvider() {
    Optional<Provider> p = ofNullable(getProvider(PROVIDER_NAME));
    if (p.isPresent())
      return p.get();
    System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "true");
    Provider provider = new BouncyCastleProvider();
    addProvider(provider);
    return provider;
  }
  
  public static Provider installJsr105Provider() {
    Optional<Provider> p = ofNullable(getProvider(JSR_105_PROVIDER));
    if (p.isPresent())
      return p.get();
    Provider provider = new XMLDSigRI();
    addProvider(provider);
    return provider;
  }
  
  public static AuthProvider installSunPKCS11Provider(String providerName, InputStream config) {
    Optional<Provider> p = ofNullable(getProvider("SunPKCS11-" + providerName));
    if (p.isPresent())
      return (AuthProvider)p.get();
    @SuppressWarnings("restriction")
    sun.security.pkcs11.SunPKCS11 provider = new sun.security.pkcs11.SunPKCS11(config);
    Security.addProvider(provider);
    return provider;
  }

  public static void logoutAndUninstall(Provider provider) {
    if (provider == null)
      return;
    if (provider instanceof AuthProvider)
      tryRun(() -> ((AuthProvider)provider).logout());
    tryRun(() -> provider.clear());
    tryRun(() -> removeProvider(provider.getName()));
  }
}
