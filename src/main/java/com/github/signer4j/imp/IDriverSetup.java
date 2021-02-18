package com.github.signer4j.imp;

import java.nio.file.Path;

interface IDriverSetup {
  Path getLibrary();
  
  String getMd5();
}