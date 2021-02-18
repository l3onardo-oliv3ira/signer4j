package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;
import static com.github.signer4j.imp.KeyStoreInvokeHandler.INVOKER;
import static java.util.Optional.ofNullable;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import com.github.signer4j.IDevice;
import com.github.signer4j.IKeyStore;
import com.github.signer4j.imp.exception.KeyStoreAccessException;
import com.github.signer4j.imp.exception.PrivateKeyNotFound;

abstract class AbstractKeyStore extends ExceptionExpert implements IKeyStore {
  
  private boolean closed = false;
  
  protected final KeyStore keyStore;
  
  private final IDevice device;
  
  private final Runnable dispose;
  
  protected AbstractKeyStore(IDevice device, KeyStore keystore, Runnable dispose) throws PrivateKeyNotFound  {
    this.device = requireNonNull(device, "device is null");
    this.keyStore = requireNonNull(keystore, "null keystore is not supported");
    this.dispose = requireNonNull(dispose, "dispose is null");
    this.setup();
  }
  
  protected void setup() throws PrivateKeyNotFound {
    if (!checkIfHasPrivateKey()) {
      throw new PrivateKeyNotFound("KeyStore don't offer alias access to private key!");
    }
  }
  
  protected void checkIfAvailable() {
    if (isClosed()) {
      throw new IllegalStateException("Unabled to access closed KeyStore");
    }
  }
  
  private boolean checkIfHasPrivateKey() {
    try {
      Enumeration<String> e = keyStore.aliases();
      while (e.hasMoreElements()) {
        String alias = e.nextElement();
        if(keyStore.isKeyEntry(alias)) {
          return true;
        }
      }
    } catch (Exception ex) {
      handleException(ex);
    }
    return false;
  }
  
  @Override
  public final IDevice getDevice() {
    return device;
  }
  
  @Override
  public final Enumeration<String> getAliases() throws KeyStoreAccessException {
    try {
      return keyStore.aliases();
    } catch (KeyStoreException e) {
      dispose.run();
      throw new KeyStoreAccessException("Não foi possível ler os aliases do keystore", e);
    }
  }

  @Override
  public final boolean isClosed() {
    return closed;
  }
  
  private final <T> T invoke(Supplier<T> tryBlock) throws KeyStoreAccessException {
    return INVOKER.invoke(tryBlock, (e) -> dispose.run()); //automaticaly logout if exception occurr!
  }
  
  @Override
  public final Certificate getCertificate(String alias) throws KeyStoreAccessException {
    checkIfAvailable();
    return invoke(() -> this.keyStore.getCertificate(alias));
  }
  
  @Override
  public final List<Certificate> getCertificateChain(String alias) throws KeyStoreAccessException {
    checkIfAvailable();
    return Arrays.asList(invoke(() -> (Certificate[])this.keyStore.getCertificateChain(alias)));
  }

  @Override
  public final String getCertificateAlias(Certificate certificate) throws KeyStoreAccessException {
    checkIfAvailable();
    return invoke(() -> this.keyStore.getCertificateAlias(certificate));
  }
  
  @Override
  public final PrivateKey getPrivateKey(String alias, char[] password) throws KeyStoreAccessException {
    checkIfAvailable();
    return invoke(() -> ofNullable((PrivateKey)this.keyStore.getKey(alias, password))
      .orElseThrow(() -> new PrivateKeyNotFound("KeyStore return's null private key for alias: " + alias))
    );
  }
  
  @Override
  public String getProvider() throws KeyStoreAccessException {
    checkIfAvailable();
    return invoke(() -> keyStore.getProvider().getName());
  }
  
  @Override
  public final void close() throws Exception {
    if (!isClosed()) {
      try {
        doClose();
      } catch(Exception e) {
        handleException(e);
      }finally {
        this.closed = true;
      }
    }
  }
  
  protected void doClose() throws Exception {}
}
