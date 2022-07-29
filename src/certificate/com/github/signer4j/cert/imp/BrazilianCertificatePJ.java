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


package com.github.signer4j.cert.imp;

import java.util.Optional;

import com.github.signer4j.cert.ICertificatePJ;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_2;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_3;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_4;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_7;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_8;
import com.github.utils4j.imp.Args;

class BrazilianCertificatePJ implements ICertificatePJ {

  private OID_2_16_76_1_3_2 id_2_16_76_1_3_2 = null;
  private OID_2_16_76_1_3_3 id_2_16_76_1_3_3 = null;
  private OID_2_16_76_1_3_4 id_2_16_76_1_3_4 = null;
  private OID_2_16_76_1_3_7 id_2_16_76_1_3_7 = null;
  private OID_2_16_76_1_3_8 id_2_16_76_1_3_8 = null;

  public BrazilianCertificatePJ(OID_2_16_76_1_3_2 oid1, OID_2_16_76_1_3_3 oid2, OID_2_16_76_1_3_4 oid3, OID_2_16_76_1_3_7 oid4, OID_2_16_76_1_3_8 oid5) {
    this.id_2_16_76_1_3_2 = Args.requireNonNull(oid1, "oid1 is null");
    this.id_2_16_76_1_3_3 = Args.requireNonNull(oid2, "oid2 is null");
    this.id_2_16_76_1_3_4 = Args.requireNonNull(oid3, "oid3 is null");
    this.id_2_16_76_1_3_7 = Args.requireNonNull(oid4, "oid4 is null");
    this.id_2_16_76_1_3_8 = Args.requireNonNull(oid5, "oid5 is null");
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
