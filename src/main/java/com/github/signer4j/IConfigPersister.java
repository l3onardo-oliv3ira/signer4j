package com.github.signer4j;

import java.util.Optional;

import com.github.signe4j.imp.function.Performable;

public interface IConfigPersister {

  Optional<String> defaultCertificate();

  Optional<String> defaultDevice();

  Optional<String> defaultAlias();

  void saveA1Paths(IFilePath... path);

  void saveA3Paths(IFilePath... path);

  void loadA1Paths(Performable<IFilePath> add); 

  void loadA3Paths(Performable<IFilePath> add);

  void save(String defaultAlias);
  
  void reset();

}