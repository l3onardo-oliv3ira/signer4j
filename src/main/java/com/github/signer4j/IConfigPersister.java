package com.github.signer4j;

import java.util.Optional;
import java.util.function.Consumer;

public interface IConfigPersister {

  Optional<String> defaultCertificate();

  Optional<String> defaultDevice();

  Optional<String> defaultAlias();

  void saveA1Paths(IFilePath... path);

  void saveA3Paths(IFilePath... path);

  void loadA1Paths(Consumer<IFilePath> add); 

  void loadA3Paths(Consumer<IFilePath> add);

  void save(String defaultAlias);
  
  void reset();

}