package com.github.signer4j.imp;

import static com.github.utils4j.imp.Throwables.tryRuntime;
import static java.util.stream.Collectors.toList;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.github.signer4j.ICertificate;
import com.github.signer4j.ICertificateChooser;
import com.github.signer4j.ICertificates;
import com.github.signer4j.IChoice;
import com.github.signer4j.IDevice;
import com.github.signer4j.IKeyStoreAccess;
import com.github.signer4j.imp.exception.InterruptedSigner4JRuntimeException;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Args;

public abstract class AbstractCertificateChooser implements ICertificateChooser {
  
  private final IKeyStoreAccess keyStore;
  
  private final ICertificates certificates;
  
  private List<CertificateEntry> options;
  
  protected AbstractCertificateChooser(IKeyStoreAccess keyStore, ICertificates certificates) {
    this.keyStore = Args.requireNonNull(keyStore, "keystore is null");
    this.certificates = Args.requireNonNull(certificates, "certificates is null");
  }
  
  private Optional<IDevice> getDevice() {
    return keyStore.getDevice();
  }
  
  private final List<Certificate> getChain(String alias) throws Signer4JException{
    return keyStore.getCertificateChain(alias);
  }

  private final Certificate getCertificate(String alias) throws Signer4JException {
    return keyStore.getCertificate(alias);
  }
  
  private final PrivateKey gePrivateKey(String alias) throws Signer4JException {
    return keyStore.getPrivateKey(alias);
  }
  
  private final String getAliasFrom(ICertificate certificate) throws Signer4JException {
    return keyStore.getCertificateAlias(certificate.toX509());
  }
  
  private final String getProvider() throws Signer4JException {
    return keyStore.getProvider();
  }
  
  protected static class CertificateEntry extends DefaultCertificateEntry {
    public final String aliasName;
    
    public CertificateEntry(Optional<IDevice> device, String name, ICertificate certificate) {
      super(device, certificate);
      this.aliasName = name;
    }
  }
  
  @Override
  public final IChoice choose() throws Signer4JException {
    if (this.options == null) {
      this.options = certificates.stream()
        .filter(getPredicate())
        .map(c -> tryRuntime(() -> new CertificateEntry(getDevice(), getAliasFrom(c), c), InterruptedSigner4JRuntimeException::of)
      ).collect(toList());
    }
    return doChoose(options);
  }
  
  protected Predicate<ICertificate> getPredicate() {
    return c -> c.getKeyUsage().isDigitalSignature();
  }
  
  protected final IChoice toChoice(CertificateEntry choice) throws Signer4JException {
    return toChoice(choice.aliasName);
  }

  protected final IChoice toChoice(String choosedAlias) throws Signer4JException {
    return Choice.from(
      gePrivateKey(choosedAlias),
      getCertificate(choosedAlias),
      getChain(choosedAlias),
      getProvider()
    );
  }
  
  protected abstract IChoice doChoose(List<CertificateEntry> options) throws Signer4JException;
}
