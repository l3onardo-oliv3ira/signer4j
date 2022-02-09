package com.github.signer4j.cert.imp;

import static com.github.signer4j.imp.Args.requireNonNull;
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
import com.github.signer4j.imp.Certificates;
import com.github.signer4j.imp.Strings;

class BrazilianCertificate implements ICertificate {

  private X509Certificate certificate = null;
  private IDistinguishedName certificateFor = null;
  private IDistinguishedName certificateFrom = null;
  private KeyUsage keyUsage = null;

  private ISubjectAlternativeNames subjectAlternativeNames = null;

  public BrazilianCertificate(InputStream is) throws CertificateException {
    this(Certificates.create(is));
  }

  public BrazilianCertificate(X509Certificate certificate) {
    this.certificate = requireNonNull(certificate, "certificate is null");
  }

  @Override
  public Date getAfterDate() {
    return certificate.getNotAfter();
  }

  @Override
  public Date getBeforeDate() {
    return certificate.getNotBefore();
  }
  
  @Override
  public boolean isExpired() {
    return new Date().getTime() > getAfterDate().getTime();
  }
  
  @Override
  public String getManufacturer() {
    return getCertificateIssuerDN().getFullName();
  }

  @Override
  public String getSerial() {
    return toString(certificate.getSerialNumber());
  }

  @Override
  public X509Certificate toX509() {
    return certificate; 
  }
  
  @Override
  public IDistinguishedName getCertificateIssuerDN() {
    if (certificateFrom == null) {
      certificateFrom = new DistinguishedName(certificate.getIssuerDN().getName());
    }
    return certificateFrom;
  }

  @Override
  public IDistinguishedName getCertificateSubjectDN() {
    if (certificateFor == null) {
      certificateFor = new DistinguishedName(certificate.getSubjectDN().getName());
    }
    return certificateFor;
  }
  
  @Override
  public Optional<ICertificatePF> getCertificatePF() {
    if (getSubjectAlternativeNames() == null) {
      return Optional.empty();
    }
    return getSubjectAlternativeNames().getCertificatePF();
  }

  @Override
  public Optional<ICertificatePJ> getCertificatePJ() {
    if (getSubjectAlternativeNames() == null) {
      return Optional.empty();
    }
    return getSubjectAlternativeNames().getCertificatePJ();
  }

  @Override
  public KeyUsage getKeyUsage() {
    if (keyUsage == null) {
      keyUsage = new KeyUsage(certificate);
    }
    return keyUsage;
  }

  @Override
  public ISubjectAlternativeNames getSubjectAlternativeNames() {
    if (this.subjectAlternativeNames == null) {
      this.subjectAlternativeNames = new SubjectAlternativeNames(this.certificate);
    }
    return this.subjectAlternativeNames;
  }

  @Override
  public Optional<String> getEmail() {
    if (getSubjectAlternativeNames() == null) {
      return Optional.empty();
    }
    return getSubjectAlternativeNames().getEmail();
  }
  
  @Override
  public boolean hasCertificatePJ() {
    if (getSubjectAlternativeNames() == null) {
      return false;
    }
    return getSubjectAlternativeNames().hasCertificatePJ();
  }

  @Override
  public boolean hasCertificatePF() {
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
  
  public ASN1Primitive getExtensionValue(String oid) {
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
  public String getName() {
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
  public List<String> getCRLDistributionPoint() {
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

