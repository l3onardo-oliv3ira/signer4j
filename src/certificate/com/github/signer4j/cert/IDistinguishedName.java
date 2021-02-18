package com.github.signer4j.cert;

import java.util.Optional;

public interface IDistinguishedName {
  String getFullName();
  
  Optional<String> getProperty(String key);
}