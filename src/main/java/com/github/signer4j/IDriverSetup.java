package com.github.signer4j;

import java.nio.file.Path;

public interface IDriverSetup {
  Path getLibrary();
  
  String getMd5();
}