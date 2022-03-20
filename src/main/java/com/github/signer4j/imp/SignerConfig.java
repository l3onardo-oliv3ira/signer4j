package com.github.signer4j.imp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.signer4j.IConfig;
import com.github.utils4j.imp.Args;

public class SignerConfig implements IConfig {

  private static final Path HOME = Paths.get(System.getProperty("user.home"));
      
  private final File config;
  
  public SignerConfig() {
    this("signer4j");
  }

  public SignerConfig(String configName) {
    this(HOME.resolve("." + configName).resolve(configName + ".config").toFile());
  }
  
  public SignerConfig(File configFile) {
    Args.requireNonNull(configFile, "config is null");
    configFile.getParentFile().mkdirs();
    this.config = configFile;
  }
  
  @Override
  public void reset() {
    this.config.delete();
  }
  
  @Override
  public File getConfigFile() throws IOException {
    if (!config.exists() && !config.createNewFile()) {
      throw new IOException("Não foi possível criar o arquivo de configuração em: " + config.getCanonicalPath());
    }
    return config;
  }
}
