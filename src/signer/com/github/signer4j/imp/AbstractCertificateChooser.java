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

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Predicate;

import com.github.signer4j.ICertificate;
import com.github.signer4j.ICertificateChooser;
import com.github.signer4j.ICertificates;
import com.github.signer4j.IChoice;
import com.github.signer4j.IDevice;
import com.github.signer4j.IKeyStoreAccess;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Args;

public abstract class AbstractCertificateChooser implements ICertificateChooser {
  
  private final IKeyStoreAccess keyStore;
  
  private final ICertificates certificates;
  
  private List<DefaultCertificateEntry> options;
  
  protected AbstractCertificateChooser(IKeyStoreAccess keyStore, ICertificates certificates) {
    this.keyStore = Args.requireNonNull(keyStore, "keystore is null");
    this.certificates = Args.requireNonNull(certificates, "certificates is null");
  }
  
  private IDevice getDevice() {
    return keyStore.getDevice();
  }
  
  @Override
  public final IChoice choose() throws Signer4JException, SwitchRepositoryException {
    if (this.options == null) {
      this.options = certificates.stream().filter(getPredicate()).map(c -> new DefaultCertificateEntry(getDevice(), c)).collect(toList());
    }
    return doChoose(options);
  }
  
  protected Predicate<ICertificate> getPredicate() {
    return c -> c.getKeyUsage().isDigitalSignature();
  }
  
  protected final IChoice toChoice(DefaultCertificateEntry choice) throws Signer4JException {
    return Choice.from(keyStore, choice.getCertificate().getAlias().orElse("")); //WE HAVE TO GO BACK HERE! (orElse) 
  }
  
  protected abstract IChoice doChoose(List<DefaultCertificateEntry> options) throws Signer4JException, SwitchRepositoryException;
}
