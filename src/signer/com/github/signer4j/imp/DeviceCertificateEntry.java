package com.github.signer4j.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.github.signer4j.ICertificate;
import com.github.signer4j.IDevice;
import com.github.signer4j.ICertificateListUI.ICertificateEntry;

public class DeviceCertificateEntry extends DefaultCertificateEntry {
  
  public static List<ICertificateEntry> toEntries(List<IDevice> devices, Predicate<ICertificate> filter) {
    final List<ICertificateEntry> entries = new ArrayList<>();
    devices.forEach(d -> d.getCertificates()
      .stream()
      .filter(filter)
      .forEach(c -> entries.add(new DeviceCertificateEntry(d, c))));
    return entries;
  }
  
  private DeviceCertificateEntry(IDevice device, ICertificate certificate) {
    super(Optional.ofNullable(device), certificate);
  }
  
  public Optional<IDevice> getNative() {
    return super.device;
  }
}