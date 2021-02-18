package com.github.signer4j.imp;

import java.nio.file.Path;
import java.util.Optional;

public abstract class AbstractStrategy implements IDriverLookupStrategy{

  protected void createAndVisit(Path path, IDriverVisitor visitor) {
    Optional<DriverSetup> ds = DriverSetup.create(path);
    if (ds.isPresent()) {
      visitor.visit(ds.get());
    }
  }
}
