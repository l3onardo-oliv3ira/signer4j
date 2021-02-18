package com.github.signer4j.cert;

import java.util.Optional;

public interface ISubjectAlternativeNames {

  boolean hasCertificatePF();

  Optional<ICertificatePF> getCertificatePF();

  boolean hasCertificatePJ();

  Optional<ICertificatePJ> getCertificatePJ();

  Optional<String> getEmail();

}