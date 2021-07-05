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

import static com.github.signer4j.imp.PKCS12KeyStoreLoaderParams.CERTIFICATE_PATH_PARAM;
import static com.github.signer4j.imp.Signer4JInvoker.SIGNER4J;
import static com.github.utils4j.imp.Strings.space;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.function.Supplier;

import javax.security.auth.callback.PasswordCallback;

import com.github.signer4j.IDevice;
import com.github.signer4j.IPasswordCallbackHandler;
import com.github.signer4j.imp.exception.Pkcs12FileNotFoundException;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.IParams;
import com.github.utils4j.imp.Args;

class PKCS12KeyStoreLoader implements IKeyStoreLoader {
  
  private final IPasswordCallbackHandler handler;
  private final IDevice device;
  private final Runnable dispose;
  
  public PKCS12KeyStoreLoader(IPasswordCallbackHandler handler, IDevice device, Runnable dispose) {
    this.handler = Args.requireNonNull(handler, "Unabled to create loader with null handler");
    this.device = Args.requireNonNull(device, "device is null");
    this.dispose = Args.requireNonNull(dispose, "dispose is null");
  }
  
  @Override
  public IKeyStore getKeyStore(IParams params) throws Signer4JException {
    Args.requireNonNull(params, "params is null");
    String certPath = params.orElseThrow(CERTIFICATE_PATH_PARAM, validate());
    return getKeyStore(new File(certPath));
  }

  private static Supplier<? extends IllegalArgumentException> validate() {
    return () -> new IllegalArgumentException(CERTIFICATE_PATH_PARAM + " is undefined");
  }
  
  private IKeyStore getKeyStore(File input) throws Signer4JException {
    try(InputStream stream = new FileInputStream(input)) {
      final PasswordCallback callback = new PasswordCallback(space(), false);
      return SIGNER4J.invoke(() -> {
        handler.handle(callback);
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(stream, callback.getPassword());
        return new PKCS12KeyStore(keyStore, device, dispose, callback.getPassword());
      }, callback::clearPassword);
    } catch (FileNotFoundException e) {
      throw new Pkcs12FileNotFoundException("Arquivo não encontrado: " + input.getAbsolutePath(), e);
    } catch (IOException e) {
      throw new Signer4JException("Não foi possível ler o arquivo: " + input.getAbsolutePath(), e);
    }
  }
}
