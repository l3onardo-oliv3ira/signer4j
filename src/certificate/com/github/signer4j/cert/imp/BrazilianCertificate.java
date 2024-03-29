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

import static java.util.Optional.ofNullable;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;

import com.github.signer4j.ICertificate;
import com.github.signer4j.cert.ICertificatePF;
import com.github.signer4j.cert.ICertificatePJ;
import com.github.signer4j.cert.IDistinguishedName;
import com.github.signer4j.cert.ISubjectAlternativeNames;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Certificates;
import com.github.utils4j.imp.Strings;

class BrazilianCertificate implements ICertificate {

  private final X509Certificate certificate;

  private IDistinguishedName certificateFor = null;
  private IDistinguishedName certificateFrom = null;
  private KeyUsage keyUsage = null;

  private ISubjectAlternativeNames subjectAlternativeNames = null;

  private String aliasName;

  BrazilianCertificate(InputStream is, String aliasName) throws CertificateException {
    this(Certificates.create(is), aliasName);
  }

  BrazilianCertificate(X509Certificate certificate, String aliasName) {
    this.certificate = Args.requireNonNull(certificate, "certificate is null");
    this.aliasName = aliasName;
  } 

  @Override
  public final Optional<String> getAlias() {
    return Optional.ofNullable(aliasName);
  }
  
  @Override
  public final void setAlias(String aliasName) { //This is danger!
    this.aliasName = aliasName;
  }
  
  @Override
  public final Date getAfterDate() {
    return certificate.getNotAfter();
  }

  @Override
  public final Date getBeforeDate() {
    return certificate.getNotBefore();
  }

  @Override
  public final boolean isExpired() {
    return System.currentTimeMillis() > getAfterDate().getTime();
  }

  @Override
  public final String getManufacturer() {
    return getCertificateIssuerDN().getFullName();
  }

  @Override
  public final String getSerial() {
    return toString(certificate.getSerialNumber());
  }

  @Override
  public final X509Certificate toX509() {
    return certificate; 
  }

  @Override
  public final IDistinguishedName getCertificateIssuerDN() {
    if (certificateFrom == null) {
      certificateFrom = new DistinguishedName(certificate.getIssuerDN().getName());
    }
    return certificateFrom;
  }

  @Override
  public final IDistinguishedName getCertificateSubjectDN() {
    if (certificateFor == null) {
      certificateFor = new DistinguishedName(certificate.getSubjectDN().getName());
    }
    return certificateFor;
  }

  @Override
  public final Optional<ICertificatePF> getCertificatePF() {
    if (getSubjectAlternativeNames() == null) {
      return Optional.empty();
    }
    return getSubjectAlternativeNames().getCertificatePF();
  }

  @Override
  public final Optional<ICertificatePJ> getCertificatePJ() {
    if (getSubjectAlternativeNames() == null) {
      return Optional.empty();
    }
    return getSubjectAlternativeNames().getCertificatePJ();
  }

  @Override
  public final KeyUsage getKeyUsage() {
    if (keyUsage == null) {
      keyUsage = new KeyUsage(certificate);
    }
    return keyUsage;
  }

  @Override
  public final ISubjectAlternativeNames getSubjectAlternativeNames() {
    if (this.subjectAlternativeNames == null) {
      this.subjectAlternativeNames = new SubjectAlternativeNames(this.certificate);
    }
    return this.subjectAlternativeNames;
  }

  @Override
  public final Optional<String> getEmail() {
    if (getSubjectAlternativeNames() == null) {
      return Optional.empty();
    }
    return getSubjectAlternativeNames().getEmail();
  }

  @Override
  public final boolean hasCertificatePJ() {
    if (getSubjectAlternativeNames() == null) {
      return false;
    }
    return getSubjectAlternativeNames().hasCertificatePJ();
  }

  @Override
  public final boolean hasCertificatePF() {
    if (getSubjectAlternativeNames() == null) {
      return false;
    }
    return getSubjectAlternativeNames().hasCertificatePF();
  }

  private String toString(final BigInteger i) {
    if (i == null) {
      return null;
    }
    String out = i.toString(16);
    if (out.length() % 2 == 1) {
      out = "0" + out;
    }
    return out.toUpperCase();
  }

  public final ASN1Primitive getExtensionValue(String oid) {
    try {
      byte[] extensionValue = certificate.getExtensionValue(oid);
      if (extensionValue == null) {
        return null;
      }        
      try(ASN1InputStream ie = new ASN1InputStream(extensionValue)){
        DEROctetString oct = (DEROctetString)ie.readObject();
        try(ASN1InputStream io = new ASN1InputStream(oct.getOctets())){
          return io.readObject();
        }
      }
    } catch (Exception e) {
      return null;
    }        
  }

  @Override
  public final String getName() {
    try {
      String name = this.getCertificateSubjectDN().getProperty("CN").orElse(Strings.empty());
      int pos;
      pos = name.indexOf(':');
      if (pos > 0) {
        return name.substring(0, pos);
      }
      return name;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public final List<String> getCRLDistributionPoint() {
    ASN1Primitive primitive = getExtensionValue(Extension.cRLDistributionPoints.getId());
    if (primitive == null) {
      return Collections.emptyList();
    }
    List<String> crl = new ArrayList<>();
    CRLDistPoint crlDistPoint = CRLDistPoint.getInstance(primitive);
    DistributionPoint[] distributionPoints = crlDistPoint.getDistributionPoints();

    for (DistributionPoint distributionPoint : distributionPoints) {
      DistributionPointName dpn = distributionPoint.getDistributionPoint();
      if (dpn != null) {
        if (DistributionPointName.FULL_NAME == dpn.getType()) {
          GeneralName[] genNames = GeneralNames.getInstance(dpn.getName()).getNames();
          for (GeneralName genName : genNames) {
            if (GeneralName.uniformResourceIdentifier == genName.getTagNo()) {
              String url = DERIA5String.getInstance(genName.getName()).getString();
              crl.add(url);
            }
          }
        }
      }
    }
    return crl;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(0);
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    sb.append(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");
    append(sb, "alias", aliasName);
    append(sb, "issuerDN", getCertificateIssuerDN());
    append(sb, "subjectDN", getCertificateSubjectDN());
    append(sb, "serialNumber", getSerial());
    append(sb, "valid.from", formatter.format(getBeforeDate()));
    append(sb, "valid.to", formatter.format(getAfterDate()));
    append(sb, "name", getName());
    append(sb, "is.pf", hasCertificatePF());
    if (hasCertificatePJ()) {
      ICertificatePJ pj = getCertificatePJ().get();
      append(sb, "cnpj", pj.getCNPJ());
      append(sb, "responsible", pj.getResponsibleName());
      append(sb, "business.name", pj.getBusinessName());
      append(sb, "nis", pj.getNis());
      append(sb, "cei", pj.getCEI());
    }
    append(sb, "is.pj", Optional.of(Boolean.toString(hasCertificatePJ())));
    if (hasCertificatePF()) {
      ICertificatePF pf = getCertificatePF().get();
      append(sb, "cpf", pf.getCPF());
      append(sb, "birth.date", pf.getBirthDate().orElse(null));
      append(sb, "email", getEmail());
      append(sb, "pis", pf.getNis());
      append(sb, "rg", pf.getRg());
      append(sb, "rg.uf", pf.getUfIssuingAgencyRg());
      append(sb, "rg.issuing.agency", pf.getIssuingAgencyRg());
      append(sb, "voter.document", pf.getElectoralDocument());
      append(sb, "voter.city", pf.getCityElectoralDocument());
      append(sb, "voter.uf", pf.getUFElectoralDocument());
      append(sb, "zone", pf.getZoneElectoralDocument());
      append(sb, "section", pf.getSectionElectoralDocument());
      append(sb, "cei", pf.getCEI());
    }
    sb.append("clr: ").append("\n");
    getCRLDistributionPoint().forEach(crl -> sb.append(' ').append(crl).append("\n"));
    return sb.toString();
  }

  private static StringBuilder append(StringBuilder sb, String field, Optional<String> value) {
    return append(sb, field, value.orElse(""));
  }

  private static StringBuilder append(StringBuilder sb, String field, Object value) {
    return sb.append(field).append(": ").append(ofNullable(value).orElse("").toString()).append("\n");
  }
}

