package com.github.signer4j.imp;

import static com.github.signer4j.imp.Caller.call;
import static com.github.signer4j.imp.Caller.wrap;
import static java.util.stream.Collectors.toList;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.List;
import java.util.function.Predicate;

import com.github.signer4j.ICertificate;
import com.github.signer4j.ICertificateChooser;
import com.github.signer4j.ICertificates;
import com.github.signer4j.IDevice;
import com.github.signer4j.IKeyStoreAccess;
import com.github.signer4j.imp.exception.KeyStoreAccessException;

public abstract class AbstractCertificateChooser implements ICertificateChooser {
  
  private final IKeyStoreAccess keyStore;
  
  private final ICertificates certificates;
  
  private List<CertificateEntry> options;
  
  protected AbstractCertificateChooser(IKeyStoreAccess keyStore, ICertificates certificates) {
    this.keyStore = Args.requireNonNull(keyStore, "keystore is null");
    this.certificates = Args.requireNonNull(certificates, "certificates is null");
  }
  
  private IDevice getDevice() {
    return keyStore.getDevice();
  }
  
  private final List<Certificate> getChain(String alias) throws KeyStoreAccessException{
    return keyStore.getCertificateChain(alias);
  }

  private final Certificate getCertificate(String alias) throws KeyStoreAccessException {
    return keyStore.getCertificate(alias);
  }
  
  private final PrivateKey gePrivateKey(String alias) throws KeyStoreAccessException {
    return keyStore.getPrivateKey(alias);
  }
  
  private final String getAliasFrom(ICertificate certificate) throws KeyStoreAccessException {
    return keyStore.getCertificateAlias(certificate.getX509Certificate());
  }
  
  private final String getProvider() throws KeyStoreAccessException {
    return keyStore.getProvider();
  }
  
  protected static class CertificateEntry extends DefaultCertificateEntry {
    public final String aliasName;
    
    public CertificateEntry(IDevice device, String name, ICertificate certificate) {
      super(device, certificate);
      this.aliasName = name;
    }
  }
  
  @Override
  public final IChoice choose() throws KeyStoreAccessException {
    if (this.options == null) {
      this.options = call(
        () -> certificates
        .stream()
        .filter(getPredicate())
        .map(wrap(c -> new CertificateEntry(getDevice(), getAliasFrom(c), c)))
        .collect(toList()), 
        KeyStoreAccessException.class
      );
    }
    return doChoose(options);
  }
  
  protected Predicate<ICertificate> getPredicate() {
    return p -> p.getKeyUsage().isDigitalSignature();
  }
  
  protected final IChoice toChoice(CertificateEntry choice) throws KeyStoreAccessException {
    return toChoice(choice.aliasName);
  }

  protected final IChoice toChoice(String choosedAlias) throws KeyStoreAccessException {
    return Choice.from(
      gePrivateKey(choosedAlias),
      getCertificate(choosedAlias),
      getChain(choosedAlias),
      getProvider()
    );
  }
  
  protected abstract IChoice doChoose(List<CertificateEntry> options) throws KeyStoreAccessException;
}
