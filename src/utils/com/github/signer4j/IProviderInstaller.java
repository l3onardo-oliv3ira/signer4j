package com.github.signer4j;

import java.security.Provider;

public interface IProviderInstaller {
  default Provider install() {
    return install(null, null);
  }
  
  Provider install(String providerName, Object config);
}
