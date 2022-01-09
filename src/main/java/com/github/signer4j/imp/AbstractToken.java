package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;
import static java.util.Optional.ofNullable;

import com.github.signer4j.ICMSSignerBuilder;
import com.github.signer4j.ICertificateChooser;
import com.github.signer4j.ICertificateChooserFactory;
import com.github.signer4j.ICertificates;
import com.github.signer4j.IKeyStoreAccess;
import com.github.signer4j.IPKCS7SignerBuilder;
import com.github.signer4j.IPasswordCallbackHandler;
import com.github.signer4j.ISignerBuilder;
import com.github.signer4j.ISlot;
import com.github.signer4j.IToken;
import com.github.signer4j.TokenType;
import com.github.signer4j.cert.ICertificateFactory;
import com.github.signer4j.cert.imp.CertificateFactory;
import com.github.signer4j.exception.DriverException;
import com.github.signer4j.exception.NotAuthenticatedException;
import com.github.signer4j.imp.exception.Signer4JException;

abstract class AbstractToken<S extends ISlot> extends ExceptionExpert implements IToken {
  
  protected final transient S slot;
  
  protected final TokenType type;
  
  protected String label;
  protected String model;
  protected String serial;
  protected String manufacturer;
  protected long minPinLen;
  protected long maxPinLen;
  
  private final Runnable disposeAction = () -> logout();
  
  protected ICertificates certificates;
  
  private transient IKeyStore keyStore;
  
  protected transient IPasswordCallbackHandler passwordCallback;

  protected AbstractToken(S slot, TokenType type) {
    this.slot = requireNonNull(slot, "slot is null");
    this.type = requireNonNull(type, "type is null");
  }
  
  protected final Runnable getDispose() {
    return disposeAction;
  }

  @Override
  public final TokenType getType() {
    return type;
  }
  
  @Override
  public final String getLabel() {
    return label;
  }

  @Override
  public final String getModel() {
    return model;
  }

  @Override
  public final String getSerial() {
    return serial;
  }

  @Override
  public final String getManufacturer() {
    return manufacturer;
  }

  @Override
  public final S getSlot() {
    return slot;
  }
  
  @Override
  public final IKeyStoreAccess getKeyStoreAccess() {
    return keyStore;
  }

  @Override
  public final ICertificates getCertificates() {
    return certificates;
  }

  public final boolean isAuthenticated() {
    return keyStore != null;
  }

  @Override
  public IToken login(IPasswordCallbackHandler callback) throws Signer4JException {
    if (callback == null)
      callback = passwordCallback;
    if (!isAuthenticated()) {
      try {
        doLogin(this.keyStore = getKeyStore(callback));
      }catch(Signer4JException e) {
        disposeAction.run();
        throw e;
      }
    }
    return this;
  }

  protected void doLogin(IKeyStore keyStore) throws Signer4JException { }

  @Override
  public final void logout() {
    if (isAuthenticated()) {
      try {
        this.keyStore.close();
      } catch (Exception e) {
        handleException(e);
      }finally {
        this.keyStore = null;
      }
    }
  }
  
  @Override
  public final ISignerBuilder signerBuilder()  {
    return signerBuilder(ICertificateChooserFactory.DEFAULT);
  }
  
  @Override
  public final ISignerBuilder signerBuilder(ICertificateChooserFactory factory) {
    requireNonNull(factory, "factory is null");
    checkIfAvailable();
    return createBuilder(createChooser(factory));
  }

  protected final void checkIfAvailable() {
    if (!isAuthenticated()) {
      throw new NotAuthenticatedException("Unabled to prepare signer with no authenticated token");
    }
  }
  
  @Override
  public ICMSSignerBuilder cmsSignerBuilder() {
    return cmsSignerBuilder(ICertificateChooserFactory.DEFAULT);
  }

  @Override
  public ICMSSignerBuilder cmsSignerBuilder(ICertificateChooserFactory factory) {
    requireNonNull(factory, "factory is null");
    checkIfAvailable();
    return createCMSSignerBuilder(createChooser(factory));
  }

  @Override
  public ICertificateChooser createChooser(ICertificateChooserFactory factory) {
    requireNonNull(factory, "factory is null");
    return factory.apply(this.keyStore, this.certificates);
  }
  
  @Override
  public IPKCS7SignerBuilder pkcs7SignerBuilder() {
    return pkcs7SignerBuilder(ICertificateChooserFactory.DEFAULT);
  }

  @Override
  public IPKCS7SignerBuilder pkcs7SignerBuilder(ICertificateChooserFactory factory) {
    requireNonNull(factory, "factory is null");
    checkIfAvailable();
    return createPKCS7SignerBuilder(createChooser(factory));
  }

  protected ISignerBuilder createBuilder(ICertificateChooser chooser) {
    return new SimpleSigner.Builder(chooser, getDispose());
  }
  
  private ICMSSignerBuilder createCMSSignerBuilder(ICertificateChooser chooser) {
    return new CMSSigner.Builder(chooser, getDispose());
  }
  
  private IPKCS7SignerBuilder createPKCS7SignerBuilder(ICertificateChooser chooser) {
    return new PKCS7Signer.Builder(chooser, getDispose());
  }

  protected abstract IKeyStore getKeyStore(IPasswordCallbackHandler callback) throws Signer4JException;
  
  static abstract class Builder<S extends ISlot, T extends IToken> {
    private S slot;
    private String label;
    private String model;
    private String serial;
    private String manufacturer;
    private ICertificateFactory factory = CertificateFactory.DEFAULT;
    private IPasswordCallbackHandler passwordCallback = ConsoleCallback.HANDLER;
    
    Builder(S slot) {
      this.slot = requireNonNull(slot, "Unabled to create token with null slot");
    }  
    
    public final Builder<S, T> withLabel(String label) {
      this.label = Strings.trim(label);
      return this;
    }
    
    public final Builder<S, T> withModel(String model) {
      this.model = Strings.trim(model);
      return this;
    }

    public final Builder<S, T> withSerial(String serial) {
      this.serial = Strings.trim(serial);
      return this;
    }
    
    public final Builder<S, T> withManufacture(String manufacture) {
      this.manufacturer = Strings.trim(manufacture);
      return this;
    }
    
    public final Builder<S, T> withCertificateFactory(ICertificateFactory factory) {
      this.factory = ofNullable(factory).orElse(this.factory);
      return this;
    }
    
    public final Builder<S, T> withPasswordCallback(IPasswordCallbackHandler callback) {
      this.passwordCallback = ofNullable(callback).orElse(this.passwordCallback);
      return null;
    }
    
    @SuppressWarnings("unchecked")
    public final T build() throws DriverException {
      AbstractToken<S> token = createToken(this.slot);
      initialize(token);
      return (T)token.loadCertificates(factory);
    }

    protected void initialize(AbstractToken<S> token) {
      token.label = this.label;
      token.model = this.model;
      token.serial = this.serial;
      token.manufacturer = this.manufacturer;
      token.passwordCallback = this.passwordCallback;
    }

    protected abstract AbstractToken<S> createToken(S slot) throws DriverException;

  }
  
  protected abstract IToken loadCertificates(ICertificateFactory factory) throws DriverException;
}