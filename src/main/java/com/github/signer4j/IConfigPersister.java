package com.github.signer4j;

import java.util.Optional;

import com.github.signer4j.imp.Exec;

public interface IConfigPersister {

  Optional<String> defaultCertificate();

  Optional<String> defaultDevice();

  Optional<String> defaultAlias();

  void saveA1Paths(IFilePath... path);

  void saveA3Paths(IFilePath... path);

  void loadA1Paths(Exec<IFilePath> add); 

  void loadA3Paths(Exec<IFilePath> add);

  void save(String defaultAlias);
  
  void reset();

}