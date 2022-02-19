package com.github.signer4j.imp;

import static com.github.signer4j.imp.Throwables.tryRun;
import static org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;

import java.security.AuthProvider;
import java.security.Provider;
import java.security.Security;

import org.apache.hc.core5.function.Supplier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jcp.xml.dsig.internal.dom.XMLDSigRI;

import com.github.signer4j.IProviderInstaller;

@SuppressWarnings("restriction")
public enum ProviderInstaller implements IProviderInstaller{

  JSR105(){
    private final String JSR_105_PROVIDER = "XMLDSig"; //org.jcp.xml.dsig.internal.dom.XMLDSigRI
    @Override
    public Provider install(String providerName, Object config) {
      return setup(JSR_105_PROVIDER, () -> new XMLDSigRI());
    }
  },
  
  BC(){
    @Override
    public Provider install(String providerName, Object config) {
      return setup(PROVIDER_NAME, () -> {
        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "true");
        return new BouncyCastleProvider();
      });
    }
  },
  
  SUNPKCS11(){
    @Override
    public Provider install(String providerName, Object config) {
      Args.requireText(providerName, "provider name is empty");
      final String sunProviderName = "SunPKCS11-" + providerName;
      return setup(sunProviderName, () -> SunPKCS11Creator.create(config.toString()));
    }
  };
  
  protected Provider setup(String providerName, Supplier<Provider> supplier) {
    Provider provider = Security.getProvider(providerName);
    if (provider != null)
      return provider;
    Security.addProvider(provider = supplier.get());
    return provider;
  }
  
  public static void uninstall(Provider provider) {
    if (provider == null)
      return;
    if (provider instanceof AuthProvider)
      tryRun(() -> ((AuthProvider)provider).logout());
    tryRun(() -> provider.clear());
    tryRun(() -> Security.removeProvider(provider.getName()));
  }
}
