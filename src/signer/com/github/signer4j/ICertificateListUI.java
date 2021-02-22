package com.github.signer4j;

import java.util.List;
import java.util.Optional;

public interface ICertificateListUI {
  
  static interface ICertificateEntry {
    String getDevice();
    String getName();
    String getIssuer();
    String getDate();
    String getId();
    boolean isRemembered();
    boolean isValid();
    void setRemembered(boolean value);
  }

  Optional<ICertificateEntry> choose(List<ICertificateEntry> entries);
}
