package com.github.signer4j.imp;

import static com.github.utils4j.imp.Args.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.signer4j.ICertificate;
import com.github.signer4j.ICertificates;
import com.github.signer4j.cert.ICertificateFactory;

abstract class AbstractCertificates implements ICertificates {

  protected final ICertificateFactory factory;

  protected final List<ICertificate> certificates = new ArrayList<>();

  public AbstractCertificates(ICertificateFactory factory) {
    this.factory = requireNonNull(factory, "factory is null");
  }

  @Override
  public final int size() {
    return certificates.size();
  }

  @Override
  public final List<ICertificate> toList() {
    return Collections.unmodifiableList(certificates);
  }
}