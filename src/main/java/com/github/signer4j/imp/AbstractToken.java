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

import static com.github.signer4j.ICertificateChooserFactory.fromCertificate;
import static java.util.Optional.ofNullable;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.ICMSSignerBuilder;
import com.github.signer4j.ICertificate;
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
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Strings;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

abstract class AbstractToken<S extends ISlot> implements IToken {
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractToken.class);
  
  private final transient S slot;
  
  private final TokenType type;
  
  protected String label;
  protected String model;
  protected String serial;
  protected String manufacturer;
  protected long minPinLen;
  protected long maxPinLen;
  
  private final BehaviorSubject<Boolean> status = BehaviorSubject.create();
  
  private final Runnable disposeAction = this::logout;
  
  protected ICertificates certificates;
  
  private transient volatile IKeyStore keyStore;
  
  private Optional<ICertificate> defaultCert = Optional.empty();
  
  protected transient IPasswordCallbackHandler passwordCallback;

  protected AbstractToken(S slot, TokenType type) {
    this.slot = Args.requireNonNull(slot, "slot is null");
    this.type = Args.requireNonNull(type, "type is null");
  }
  
  protected final Runnable getDispose() {
    return disposeAction;
  }

  @Override
  public final Observable<Boolean> getStatus() {
    return status;
  }
  
  @Override
  public final TokenType getType() {
    return type;
  }
  
  @Override
  public final String getCategory() {
    return getType().toString();
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
  public final void setDefaultCertificate(ICertificate certificate) {
    this.defaultCert = Optional.ofNullable(certificate);
  }
  
  @Override
  public final Optional<ICertificate> getDefaultCertificate() {
    return this.defaultCert;
  }

  @Override
  public final IToken login(IPasswordCallbackHandler callback) throws Signer4JException {
    if (callback == null)
      callback = passwordCallback;
    if (!isAuthenticated()) {
      try {
        doLogin(this.keyStore = getKeyStore(callback));
        this.status.onNext(true);
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
        LOGGER.warn("Unabled to logout gracefully", e);
      }finally {
        this.keyStore = null;
        this.defaultCert = Optional.empty();
        this.status.onNext(false);
      }
    }
  }
  
  @Override
  public final ISignerBuilder signerBuilder()  {
    return signerBuilder(fromCertificate(getDefaultCertificate()));
  }
  
  @Override
  public final ISignerBuilder signerBuilder(ICertificateChooserFactory factory) {
    Args.requireNonNull(factory, "factory is null");
    checkIfAvailable();
    return createBuilder(createChooser(factory));
  }

  protected final void checkIfAvailable() {
    if (!isAuthenticated()) {
      throw new NotAuthenticatedException("Unabled to prepare signer with no authenticated token");
    }
  }
  
  @Override
  public final ICMSSignerBuilder cmsSignerBuilder() {
    return cmsSignerBuilder(fromCertificate(getDefaultCertificate()));
  }

  @Override
  public final ICMSSignerBuilder cmsSignerBuilder(ICertificateChooserFactory factory) {
    Args.requireNonNull(factory, "factory is null");
    checkIfAvailable();
    return createCMSSignerBuilder(createChooser(factory));
  }

  @Override
  public final ICertificateChooser createChooser(ICertificateChooserFactory factory) {
    Args.requireNonNull(factory, "factory is null");
    checkIfAvailable();
    return factory.apply(this.keyStore, this.certificates);
  }
  
  @Override
  public final IPKCS7SignerBuilder pkcs7SignerBuilder() {
    return pkcs7SignerBuilder(fromCertificate(getDefaultCertificate()));
  }

  @Override
  public final IPKCS7SignerBuilder pkcs7SignerBuilder(ICertificateChooserFactory factory) {
    Args.requireNonNull(factory, "factory is null");
    checkIfAvailable();
    return createPKCS7SignerBuilder(createChooser(factory));
  }

  private ISignerBuilder createBuilder(ICertificateChooser chooser) {
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
    private IPasswordCallbackHandler passwordCallback = PasswordCallbackHandler.CONSOLE;
    
    Builder(S slot) {
      this.slot = Args.requireNonNull(slot, "Unabled to create token with null slot");
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
