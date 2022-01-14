package com.github.signer4j;

import java.util.List;
import java.util.Optional;

import com.github.signer4j.imp.function.BiProcedure;

public interface ICertificateListUI {
  
  interface IA1A3ConfigSaved extends BiProcedure<List<IFilePath>, List<IFilePath>> {}
  
  interface ICertificateEntry {
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
