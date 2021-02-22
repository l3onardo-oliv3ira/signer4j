package com.github.signer4j.imp;

import static java.lang.System.getProperty;
import static java.nio.file.Paths.get;

import java.io.File;
import java.io.IOException;

import com.github.signer4j.IConfig;

public class SignerConfig implements IConfig {
  
  private final File config;
  
  public SignerConfig() {
    this("signer4j");
  }

  public SignerConfig(String configName) {
    this(get(getProperty("user.home"), "." + configName, configName + ".config").toFile());
  }

  public SignerConfig(File config) {
    Args.requireNonNull(config, "config is null");
    config.getParentFile().mkdirs();
    this.config = config;
  }
  
  @Override
  public void reset() {
    this.config.delete();
  }
  
  @Override
  public File getConfigFile() throws IOException {
    if (!config.exists() && !config.createNewFile()) {
      throw new IOException("Não foi possível criar o arquivo de configuração em: " + config.getAbsolutePath());
    }
    return config;
  }
}
