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

import static com.github.signer4j.imp.Signer4JInvoker.SIGNER4J;
import static com.github.utils4j.imp.Containers.arrayList;
import static java.util.Collections.unmodifiableList;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.IDevice;
import com.github.signer4j.imp.exception.InterruptedSigner4JRuntimeException;
import com.github.signer4j.imp.exception.PrivateKeyNotFound;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.function.IProvider;

abstract class AbstractKeyStore implements IKeyStore {
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractKeyStore.class);
  
  private boolean closed = false, initKey = false;
  
  protected final KeyStore keyStore;
  
  private final IDevice device;
  
  private final Runnable dispose;
  
  protected AbstractKeyStore(KeyStore keystore, IDevice device) throws PrivateKeyNotFound  {
    this(keystore, device, () -> {});
  }
  
  protected AbstractKeyStore(KeyStore keystore, IDevice device, Runnable dispose) throws PrivateKeyNotFound  {
    this.keyStore = Args.requireNonNull(keystore, "null keystore is not supported");
    this.dispose = Args.requireNonNull(dispose, "dispose is null");
    this.device = Args.requireNonNull(device, "device is null");
  }
  
  protected final void dispose() {
    dispose.run();
  }
  
  protected void checkIfAvailable() {
    if (isClosed()) {
      throw new IllegalStateException("KeyStore fechado. Possível perda de conexão com dispositivo");
    }
  }
  
  @Override
  public final IDevice getDevice() {
    checkIfAvailable();
    return device;
  }
  
  @Override
  public final Enumeration<String> getAliases() throws Signer4JException {
    checkIfAvailable();
    try {
      return keyStore.aliases();
    } catch (KeyStoreException e) {
      this.dispose();
      throw new Signer4JException("Não foi possível ler os aliases do keystore", e);
    }
  }

  @Override
  public final boolean isClosed() {
    return closed;
  }
  
  private final <T> T invoke(IProvider<T> tryBlock) throws Signer4JException {
    return SIGNER4J.invoke(tryBlock, (e) -> this.dispose.run());  //automaticaly logout if exception occurr!
  }
  
  @Override
  public final Certificate getCertificate(String alias) throws Signer4JException {
    checkIfAvailable();
    return invoke(() -> this.keyStore.getCertificate(alias));
  }
  
  @Override
  public final String getCertificateAlias(Certificate certificate) throws Signer4JException {
    checkIfAvailable();
    return invoke(() -> this.keyStore.getCertificateAlias(certificate));
  }
  
  @Override
  public final List<Certificate> getCertificateChain(String alias) throws Signer4JException {
    checkIfAvailable();
    return unmodifiableList(arrayList(invoke(() -> this.keyStore.getCertificateChain(alias))));
  }

  @Override
  public final PrivateKey getPrivateKey(String alias, char[] password) throws Signer4JException {
    checkIfAvailable();
    return invoke(() -> {
      PrivateKey key = Optional.ofNullable((PrivateKey)this.keyStore.getKey(alias, password))
        .orElseThrow(() -> isClosed() ? 
          new InterruptedSigner4JRuntimeException() : 
          new PrivateKeyNotFound("KeyStore return's null private key for alias: " + alias)
        );
      if (!initKey) {
        onInitKey(key);
        initKey = true;
      }
      return key;
    });
  }
  
  protected void onInitKey(PrivateKey key) throws Exception {}

  @Override
  public String getProvider() throws Signer4JException {
    checkIfAvailable();
    return invoke(keyStore.getProvider()::getName);
  }
  
  @Override
  public final void close() throws Exception {
    if (!isClosed()) {
      try {
        doClose();
      } catch(Exception e) {
        LOGGER.warn("Unabled to close keystore gracefully", e);
      } finally {
        this.closed = true;
        this.initKey = false;
      }
    }
  }
  
  protected void doClose() throws Exception {}
}
