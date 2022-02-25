package com.github.signer4j.imp;

import com.github.utils4j.IExtensionProvider;

interface IModuleExtension extends IExtensionProvider {
  default String defaultModule() {
    return "pkcs11" + getExtension();
  }
}
