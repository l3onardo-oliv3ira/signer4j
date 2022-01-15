package com.github.signer4j.imp;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.github.signer4j.IDriverLookupStrategy;
import com.github.signer4j.IDriverVisitor;

abstract class PreloadedStrategy implements IDriverLookupStrategy {

  private final Set<DriverSetup> libraries = new HashSet<DriverSetup>();
  
  protected final boolean load(String library) {
    Optional<DriverSetup> ds = DriverSetup.create(Paths.get(library));
    if (ds.isPresent()) {
      return libraries.add(ds.get());
    }
    return false;
  }
  
  @Override
  public final void lookup(IDriverVisitor visitor) {
    libraries.forEach(visitor::visit);
  }
}
