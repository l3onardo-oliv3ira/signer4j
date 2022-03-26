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


package com.github.signer4j.imp;

import java.util.Optional;
import java.util.function.Function;

import com.github.signer4j.ICertificate;
import com.github.signer4j.ICertificateListUI;
import com.github.signer4j.IDevice;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Dates;

public class DefaultCertificateEntry implements ICertificateListUI.ICertificateEntry {
  
  private final boolean expired;

  protected final Optional<IDevice> device;
  protected final ICertificate certificate;
  protected final Function<String, String> formater;
  protected boolean remembered;

  public DefaultCertificateEntry(Optional<IDevice> device, ICertificate certificate) {
    this.device = Args.requireNonNull(device, "device is null");
    this.certificate = Args.requireNonNull(certificate, "certificate is null");
    this.expired = certificate.isExpired();
    this.formater = !expired ? s -> s : s -> "<html><strike style=\"color:red\">" + s + "</strike></html>";
  }
  
  @Override
  public final boolean isExpired() {
    return expired;
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
