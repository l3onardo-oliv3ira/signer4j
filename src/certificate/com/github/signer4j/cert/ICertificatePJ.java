package com.github.signer4j.cert;

import java.util.Optional;

public interface ICertificatePJ {

  Optional<String> getResponsibleName();

  Optional<String> getResponsibleCPF();

  Optional<String> getCNPJ();

  Optional<String> getBirthDate();

  Optional<String> getBusinessName();

  Optional<String> getNis();

  Optional<String> getRg();

  Optional<String> getIssuingAgencyRg();

  Optional<String> getUfIssuingAgencyRg();

  Optional<String> getCEI();
}