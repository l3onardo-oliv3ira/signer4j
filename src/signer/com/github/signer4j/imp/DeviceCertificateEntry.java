package com.github.signer4j.imp;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.github.signer4j.ICertificate;
import com.github.signer4j.ICertificateListUI.ICertificateEntry;
import com.github.signer4j.IDevice;

public class DeviceCertificateEntry extends DefaultCertificateEntry {
  
  public static List<ICertificateEntry> toEntries(List<IDevice> devices, Predicate<ICertificate> filter) {
    return devices.stream()
      .map(d -> d.getCertificates().filter(filter).map(c -> new DeviceCertificateEntry(d, c)).collect(toList()))
      .flatMap(Collection::stream)
      .collect(toList());
  }

  private DeviceCertificateEntry(IDevice device, ICertificate certificate) {
    super(Optional.ofNullable(device), certificate);
  }
  
  public Optional<IDevice> getNative() {
    return super.device;
  }
}