package com.github.signer4j;
import java.io.File;
import java.io.IOException;

public interface IConfig {
  File getConfigFile() throws IOException;
  
  void reset();
}
