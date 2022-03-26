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

import static java.lang.Integer.parseInt;
import static java.time.LocalDate.of;

import java.time.LocalDate;
import java.util.Optional;

import com.github.signer4j.cert.ICertificatePF;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_1;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_5;
import com.github.signer4j.cert.oid.OID_2_16_76_1_3_6;

class BrazilianCertificatePF implements ICertificatePF {
  
  private OID_2_16_76_1_3_1 id_2_16_76_1_3_1 = null;
  private OID_2_16_76_1_3_5 id_2_16_76_1_3_5 = null;
  private OID_2_16_76_1_3_6 id_2_16_76_1_3_6 = null;

  public BrazilianCertificatePF(OID_2_16_76_1_3_1 oid1, OID_2_16_76_1_3_5 oid2, OID_2_16_76_1_3_6 oid3) {
    this.id_2_16_76_1_3_1 = oid1;
    this.id_2_16_76_1_3_5 = oid2;
    this.id_2_16_76_1_3_6 = oid3;
  }

  @Override
  public final Optional<String> getCPF() {
    return id_2_16_76_1_3_1.getCPF();
  }

  @Override
  public final Optional<String> getNis() {
    return id_2_16_76_1_3_1.getNIS();
  }

  @Override
  public final Optional<String> getRg() {
    return id_2_16_76_1_3_1.getRg();
  }

  @Override
  public final Optional<String> getIssuingAgencyRg() {
    return id_2_16_76_1_3_1.getIssuingAgencyRg();
  }

  @Override
  public final Optional<String> getUfIssuingAgencyRg() {
    return id_2_16_76_1_3_1.getUfIssuingAgencyRg();
  }

  @Override
  public final Optional<String> getElectoralDocument() {
    return id_2_16_76_1_3_5.getElectoralDocument();
  }

  @Override
  public final Optional<String> getSectionElectoralDocument() {
    return id_2_16_76_1_3_5.getSection();
  }

  @Override
  public final Optional<String> getZoneElectoralDocument() {
    return id_2_16_76_1_3_5.getZone();
  }

  @Override
  public final Optional<String> getCityElectoralDocument() {
    return id_2_16_76_1_3_5.getCityUF();
  }

  @Override
  public final Optional<String> getUFElectoralDocument() {
    return id_2_16_76_1_3_5.getUFDocument();
  }

  @Override
  public final Optional<String> getCEI() {
    return id_2_16_76_1_3_6.getCEI();
  }
  
  @Override
  public final Optional<LocalDate> getBirthDate() {
    Optional<String> date = id_2_16_76_1_3_1.getBirthDate();
    if (date.isPresent()) {
      String value = date.get();
      String day = value.substring(0, 2);
      String month = value.substring(2, 4);
      String year = value.substring(4, value.length());
      return Optional.of(of(parseInt(year), parseInt(month), parseInt(day)));
    }
    return Optional.empty();
  }

}
