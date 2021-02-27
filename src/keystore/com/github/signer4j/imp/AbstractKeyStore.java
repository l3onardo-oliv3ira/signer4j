package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;
import static com.github.signer4j.imp.Signer4JInvoker.INVOKER;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.ofNullable;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import com.github.signer4j.IDevice;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.signer4j.imp.exception.PrivateKeyNotFound;

abstract class AbstractKeyStore extends ExceptionExpert implements IKeyStore {
  
  private boolean closed = false;
  
  protected final KeyStore keyStore;
  
  private final Optional<IDevice> device;
  
  private final Runnable dispose;
  
  protected AbstractKeyStore(KeyStore keystore) throws PrivateKeyNotFound  {
    this(keystore, () -> {});
  }

  protected AbstractKeyStore(KeyStore keystore, Runnable dispose) throws PrivateKeyNotFound  {
    this(keystore, dispose, null);
  }
  
  protected AbstractKeyStore(KeyStore keystore, Runnable dispose, IDevice device) throws PrivateKeyNotFound  {
    this.keyStore = requireNonNull(keystore, "null keystore is not supported");
    this.dispose = requireNonNull(dispose, "dispose is null");
    this.device = Optional.ofNullable(device);
    this.setup();
  }
  
  protected Runnable getDispose() {
    return dispose;
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
  public final Optional<IDevice> getDevice() {
    return device;
  }
  
  @Override
  public final Enumeration<String> getAliases() throws Signer4JException {
    try {
      return keyStore.aliases();
    } catch (KeyStoreException e) {
      getDispose().run();
      throw new Signer4JException("Não foi possível ler os aliases do keystore", e);
    }
  }

  @Override
  public final boolean isClosed() {
    return closed;
  }
  
  private final <T> T invoke(Supplier<T> tryBlock) throws Signer4JException {
    return INVOKER.invoke(tryBlock, (e) -> getDispose().run()); //automaticaly logout if exception occurr!
  }
  
  @Override
  public final Certificate getCertificate(String alias) throws Signer4JException {
    checkIfAvailable();
    return invoke(() -> this.keyStore.getCertificate(alias));
  }
  
  @Override
  public final List<Certificate> getCertificateChain(String alias) throws Signer4JException {
    checkIfAvailable();
    return unmodifiableList(asList(invoke(() -> (Certificate[])this.keyStore.getCertificateChain(alias))));
  }

  @Override
  public final String getCertificateAlias(Certificate certificate) throws Signer4JException {
    checkIfAvailable();
    return invoke(() -> this.keyStore.getCertificateAlias(certificate));
  }
  
  @Override
  public final PrivateKey getPrivateKey(String alias, char[] password) throws Signer4JException {
    checkIfAvailable();
    return invoke(() -> ofNullable((PrivateKey)this.keyStore.getKey(alias, password))
      .orElseThrow(() -> new PrivateKeyNotFound("KeyStore return's null private key for alias: " + alias))
    );
  }
  
  @Override
  public String getProvider() throws Signer4JException {
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
