package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import com.github.signer4j.IDriverVisitor;

public class EnvironmentStrategy extends AbstractStrategy {

  private static final String VAR_PKCS11_DRIVER = "PKCS11_DRIVER";
  
  private SystemSupport support;

  public EnvironmentStrategy() {
    this(SystemSupport.getDefault());
  }
  
  public EnvironmentStrategy(SystemSupport support) {
    this.support = requireNonNull(support, "support can't be null");
  }

  @Override
  public void lookup(IDriverVisitor visitor) {
    Optional<Path> path = read(VAR_PKCS11_DRIVER);
    if (path.isPresent()) {
      createAndVisit(path.get(), visitor);
    }
  }

  private Optional<Path> read(final String key) {
    String value = System.getenv(key);
    if (value == null) {
      value = System.getProperty(key);
    }
    if (value == null) {
      value = System.getProperty(key.toLowerCase());
    }
    if (value == null) {
      value = System.getProperty(key.toUpperCase());
    }
    if (value == null) {
      value = System.getenv(key.toLowerCase());
    }
    if (value == null) {
      value = System.getenv(key.toUpperCase());
    }
    if (value == null) {
      value = System.getProperty("user.home") + System.getProperty("file.separator") + support.defaultModule();
    }
    
    if (value != null) {
      final File file = new File(value);
      if (file.exists()) {
        return Optional.of(file.toPath().toAbsolutePath());
      }
    }
    return Optional.empty();
  }   
}
