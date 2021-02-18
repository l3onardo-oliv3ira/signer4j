package com.github.signer4j.cert;

import java.time.LocalDate;
import java.util.Optional;

public interface ICertificatePF {

  Optional<String> getCPF();

  Optional<LocalDate> getBirthDate();

  Optional<String> getNis();

  Optional<String> getRg();

  Optional<String> getIssuingAgencyRg();

  Optional<String> getUfIssuingAgencyRg();

  Optional<String> getElectoralDocument();

  Optional<String> getSectionElectoralDocument();

  Optional<String> getZoneElectoralDocument();

  Optional<String> getCityElectoralDocument();

  Optional<String> getUFElectoralDocument();

  Optional<String> getCEI();
}