package com.github.signer4j.imp;

import java.nio.file.Path;
import java.util.Optional;

import com.github.signer4j.IDriverVisitor;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Environment;

public class EnvironmentStrategy extends AbstractStrategy {

  private static final String VAR_PKCS11_DRIVER = "PKCS11_DRIVER";
  
  private IModuleExtension support;

  public EnvironmentStrategy() {
    this(SystemSupport.getDefault());
  }
  
  public EnvironmentStrategy(IModuleExtension support) {
    this.support = Args.requireNonNull(support, "support can't be null");
  }

  @Override
  public void lookup(IDriverVisitor visitor) {
    Optional<Path> path = Environment.resolveTo(VAR_PKCS11_DRIVER, support.defaultModule(), true, true);
    if (path.isPresent()) {
      createAndVisit(path.get(), visitor);
    }
  }
}
