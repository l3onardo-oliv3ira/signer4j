/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package com.github.signer4j.provider;

import static com.github.utils4j.imp.Throwables.tryRuntime;
import static java.security.Security.addProvider;
import static java.security.Security.getProvider;
import static java.security.Security.removeProvider;

import java.security.AuthProvider;
import java.security.Provider;
import java.security.Security;
import java.util.function.Supplier;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jcp.xml.dsig.internal.dom.XMLDSigRI;

import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Strings;
import com.github.utils4j.imp.Throwables;

@SuppressWarnings("restriction")
public enum ProviderInstaller {

  JSR105("XMLDSig"){ //org.jcp.xml.dsig.internal.dom.XMLDSigRI
    @Override
    public Provider install(String providerName, Object config) {
      return setup(defaultName(), XMLDSigRI::new);
    }
  },
  
  MSCAPI("SunMSCAPI") {
    @Override
    public Provider install(String providerName, Object config) {
      return setup(defaultName(), () -> tryRuntime(() -> (Provider)Class.forName("sun.security.mscapi." + defaultName()).newInstance()));
    }
  },
  
  BC(BouncyCastleProvider.PROVIDER_NAME){
    @Override
    public Provider install(String providerName, Object config) {
      return setup(defaultName(), () -> {
        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "true");
        return new BouncyCastleProvider();
      });
    }
  },
  
  SIGNER4J("Signer4J") {
    @Override
    public Provider install(String providerName, Object config) {
      return setup(defaultName(), Signer4JProvider::new);
    }
  },
  
  SUNPKCS11(){
    @Override
    public Provider install(String providerName, Object config) {
      Args.requireText(providerName, "provider name is empty"); //provider suffix
      Args.requireNonNull(config, "config is null");
      final String sunProviderName = SunPKCS11Creator.providerName(providerName);
      return setup(sunProviderName, () -> SunPKCS11Creator.create(config.toString()));
    }
  }; 
  
  private final String name;
  
  ProviderInstaller() {
    this(Strings.empty());
  }
  
  ProviderInstaller(String name) {
    this.name = Args.requireNonNull(name, "name is null");
  }
  
  public String defaultName() {
    return this.name;
  }
  
  public Provider install() {
    return install(null, null);
  }
  
  public abstract Provider install(String providerName, Object config);

  public static void uninstall(Provider provider) {
    if (provider == null) {
      return;
    }
    
    if (provider instanceof AuthProvider) {
      Throwables.runQuietly(((AuthProvider) provider)::logout);
    }
    
    Throwables.runQuietly(() -> removeProvider(provider.getName()));
  }
  
  protected Provider setup(String providerName, Supplier<Provider> supplier) {

    Provider provider = getProvider(providerName);    
    if (provider != null) {
      return provider;
    }

    provider = supplier.get();
    if (provider == null) {
      throw new RuntimeException("Unabled to create provider " + providerName);
    }

    if (provider instanceof AuthProvider) {
      Throwables.runQuietly(((AuthProvider) provider)::logout);
    }
    
    int code = addProvider(provider);
    if (code < 0) {
      throw new RuntimeException("Unabled to install provider " + providerName);
    }
    return provider;
  }  
  
  static {
    Security.removeProvider(MSCAPI.defaultName());
  }
}
