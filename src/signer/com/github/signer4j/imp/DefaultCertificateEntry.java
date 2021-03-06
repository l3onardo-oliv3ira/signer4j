package com.github.signer4j.imp;

import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

import com.github.signer4j.ICertificate;
import com.github.signer4j.ICertificateListUI;
import com.github.signer4j.IDevice;

public class DefaultCertificateEntry implements ICertificateListUI.ICertificateEntry {
  
  private final boolean valid;

  protected final Optional<IDevice> device;
  protected final ICertificate certificate;
  protected final Function<String, String> formater;
  protected boolean remembered;

  public DefaultCertificateEntry(Optional<IDevice> device, ICertificate certificate) {
    this.device = Args.requireNonNull(device, "device is null");
    this.certificate = Args.requireNonNull(certificate, "certificate is null");
    this.valid = new Date().getTime() <= certificate.getAfterDate().getTime();
    this.formater = valid ? s -> s : s -> "<html><strike style=\"color:red\">" + s + "</strike></html>";
  }
  
  @Override
  public final boolean isValid() {
    return valid;
  }

  @Override
  public final String getDevice() {
    return formater.apply(device.isPresent() ? device.get().getType() + ": " + device.get().getSerial() : "Desconhecido");
  }

  @Override
  public final String getName() {
    return formater.apply(certificate.getName());
  }

  @Override
  public final String getIssuer() {
    return formater.apply(certificate.getCertificateIssuerDN().getProperty("CN").orElse("Desconhecido"));
  }

  @Override
  public final String getDate() {
    return formater.apply(Dates.defaultFormat(certificate.getAfterDate()));
  }
  
  @Override
  public final String getId() {
    return (device.isPresent() ? device.get().getSerial() : "cert") + ":" + certificate.getSerial();
  }
  
  @Override
  public final boolean isRemembered() {
    return this.remembered;
  }
  
  @Override
  public final void setRemembered(boolean remembered) {
    this.remembered = remembered;
  }
}
