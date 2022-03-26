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


package com.github.signer4j.imp;

import static com.github.signer4j.imp.PKCS11KeyStoreLoaderParams.DRIVER_PATH_PARAM;
import static com.github.signer4j.imp.PKCS11KeyStoreLoaderParams.DRIVER_SLOT_PARAM;
import static com.github.signer4j.imp.Signer4JInvoker.SIGNER4J;
import static com.github.utils4j.imp.Args.requireNonNull;
import static com.github.utils4j.imp.Args.requireText;
import static com.github.utils4j.imp.Args.requireZeroPositive;
import static com.github.utils4j.imp.ProviderInstaller.SUNPKCS11;
import static com.github.utils4j.imp.ProviderInstaller.uninstall;
import static java.lang.String.format;

import java.security.AuthProvider;
import java.security.KeyStore;
import java.util.function.Supplier;

import com.github.signer4j.IDevice;
import com.github.signer4j.IPasswordCallbackHandler;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.IParams;
import com.github.utils4j.imp.Args;

class PKCS11KeyStoreLoader extends ExceptionExpert implements IKeyStoreLoader {
  
  private final IPasswordCallbackHandler handler;
  private final IDevice device;
  private final Runnable dispose;
  
  PKCS11KeyStoreLoader() {
    this(PasswordCallbackHandler.CONSOLE);
  }

  PKCS11KeyStoreLoader(IPasswordCallbackHandler handler) {
    this(handler, () -> {});
  }

  PKCS11KeyStoreLoader(IPasswordCallbackHandler handler, Runnable dispose) {
    this(handler, dispose, null);
  }
  
  PKCS11KeyStoreLoader(IPasswordCallbackHandler handler, Runnable dispose, IDevice device) {
    this.handler = Args.requireNonNull(handler, "Unabled to create loader with null handler");
    this.dispose = Args.requireNonNull(dispose, "dispose is null");
    this.device = device;
  }
  
  @Override
  public IKeyStore getKeyStore(IParams p) throws Signer4JException {
    requireNonNull(p, "Params is null");
    return getKeyStore(
      p.orElseThrow(DRIVER_PATH_PARAM, validate()),
      p.orElse(DRIVER_SLOT_PARAM, 0L)
    );
  }

  private IKeyStore getKeyStore(String libraryPath, long slot) throws Signer4JException {
    requireZeroPositive(slot, "slot must be 0 or positive value");
    libraryPath = requireText(libraryPath, "driver path can't be null").trim().replace('\\',  '/');
    int s = libraryPath.lastIndexOf('/');
    s = s <= -1 ? 0 : s + 1;
    String fileName = libraryPath.substring(s, libraryPath.length());
    return getKeyStore(
      providerName(fileName, slot),
      libraryPath,
      slot
    );
  }

  private IKeyStore getKeyStore(String providerName, String libraryPath, long slot) throws Signer4JException {
    return getKeyStore(
      providerName,
      format(//TODO we have to go back here for aditional parameters
        "name = %s\r\nlibrary = %s\r\nslot = %s\rattributes = compatibility", 
        providerName,
        libraryPath,
        slot
      ).toString()
    );
  }
  
  private IKeyStore getKeyStore(String providerName, String configString) throws Signer4JException {
    final AuthProvider provider = (AuthProvider)SUNPKCS11.install(providerName, configString);
    return SIGNER4J.invoke(
      () -> {
        provider.login(null, this.handler);
        KeyStore keyStore = KeyStore.getInstance("PKCS11",  provider);
        keyStore.load(null, null);
        return new PKCS11KeyStore(keyStore, dispose, device);
      }, 
      (exception) -> {
        try {
          handleException(exception);
        }finally {
          uninstall(provider);
        }
      });
  }

  private static Supplier<? extends IllegalArgumentException> validate() {
    return () -> new IllegalArgumentException(DRIVER_PATH_PARAM + " is undefined");
  }
  
  private static String providerName(String fileName, long slot) {
    return format("%s-slot%s", fileName, slot);
  }
}
