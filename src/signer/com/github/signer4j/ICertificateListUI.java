package com.github.signer4j;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.utils4j.imp.function.BiProcedure;

public interface ICertificateListUI {
  
  interface IA1A3ConfigSaved extends BiProcedure<List<IFilePath>, List<IFilePath>> {
    IA1A3ConfigSaved NOTHING = (a, b) -> {};
  }
  
  interface IChoice extends Supplier<Optional<ICertificateEntry>>{
    IChoice NEED_RELOAD = () -> Optional.empty();
  }
  
  interface ICertificateEntry {
    String getDevice();
    String getName();
    String getIssuer();
    String getDate();
    String getId();
    boolean isRemembered();
    boolean isExpired();
    void setRemembered(boolean value);
  }

  IChoice choose(List<ICertificateEntry> entries);
}
