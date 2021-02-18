package com.github.signer4j.imp;

interface IModuleExtension {
  String getExtension();
  
  default String defaultModule() {
    return "pkcs11" + getExtension();
  }
}
