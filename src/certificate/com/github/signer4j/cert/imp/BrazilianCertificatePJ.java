package com.github.signer4j.cert.imp;

import java.util.Optional;

import com.github.signer4j.cert.ICertificatePJ;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_2;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_3;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_4;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_7;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_8;

class BrazilianCertificatePJ implements ICertificatePJ {

  private OID_2_16_76_1_3_2 id_2_16_76_1_3_2 = null;
  private OID_2_16_76_1_3_3 id_2_16_76_1_3_3 = null;
  private OID_2_16_76_1_3_4 id_2_16_76_1_3_4 = null;
  private OID_2_16_76_1_3_7 id_2_16_76_1_3_7 = null;
  private OID_2_16_76_1_3_8 id_2_16_76_1_3_8 = null;

  public BrazilianCertificatePJ(OID_2_16_76_1_3_2 oid1, OID_2_16_76_1_3_3 oid2, OID_2_16_76_1_3_4 oid3, OID_2_16_76_1_3_7 oid4, OID_2_16_76_1_3_8 oid5) {
    this.id_2_16_76_1_3_2 = oid1;
    this.id_2_16_76_1_3_3 = oid2;
    this.id_2_16_76_1_3_4 = oid3;
    this.id_2_16_76_1_3_7 = oid4;
    this.id_2_16_76_1_3_8 = oid5;
  }

  @Override
  public final Optional<String> getResponsibleName() {
    return id_2_16_76_1_3_2.getName();
  }

  @Override
  public final Optional<String> getResponsibleCPF() {
    return id_2_16_76_1_3_4.getCPF();
  }

  @Override
  public final Optional<String> getCNPJ() {
    return id_2_16_76_1_3_3.getCNPJ();
  }

  @Override
  public final Optional<String> getBirthDate() {
    return id_2_16_76_1_3_4.getBirthDate();

  }

  @Override
  public final Optional<String> getBusinessName() {
    return id_2_16_76_1_3_8.getName();
  }
  
  @Override
  public final Optional<String> getNis() {
    return id_2_16_76_1_3_4.getNIS();
  }

  @Override
  public final Optional<String> getRg() {
    return id_2_16_76_1_3_4.getRg();
  }

  @Override
  public final Optional<String> getIssuingAgencyRg() {
    return id_2_16_76_1_3_4.getIssuingAgencyRg();
  }

  @Override
  public final Optional<String> getUfIssuingAgencyRg() {
    return id_2_16_76_1_3_4.getUfIssuingAgencyRg();
  }

  @Override
  public final Optional<String> getCEI() {
    return id_2_16_76_1_3_7.getCEI();
  }
}
