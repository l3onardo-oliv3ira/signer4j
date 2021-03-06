package com.github.signer4j.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.AllowedExtensions;
import com.github.signer4j.IConfig;
import com.github.signer4j.IConfigPersister;
import com.github.signer4j.IFilePath;

public class ConfigPersister implements IConfigPersister {
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(ConfigPersister.class);
  
  protected static final char LIST_DELIMITER          = '|';
  
  private static final String CERTIFICATE_A1_LIST     = "list.a1";
  
  private static final String CERTIFICATE_A3_LIST     = "list.a3";

  private static final String DEFAULT_CERTIFICATE     = "default.certificate";
  
  private final IConfig config;
  
  public ConfigPersister() {
    this(new SignerConfig());
  }

  public ConfigPersister(IConfig config) {
    this.config = Args.requireNonNull(config, "config is null");
  }
  
  @Override
  public void reset() {
    this.config.reset();
  }
  
  @Override
  public final void loadA1Paths(Exec<IFilePath> add) {
    load(add, CERTIFICATE_A1_LIST, AllowedExtensions.CERTIFICATES);
  }
  
  @Override
  public final void loadA3Paths(Exec<IFilePath> add) {
    load(add, CERTIFICATE_A3_LIST, AllowedExtensions.LIBRARIES);
  }
  
  @Override
  public final void saveA3Paths(IFilePath... path) {
    put(p -> "", CERTIFICATE_A3_LIST, path);    
  }
  
  @Override
  public final void save(String device) {
    put(p -> "", DEFAULT_CERTIFICATE, Strings.toArray(device));
  }
  
  @Override
  public final void saveA1Paths(IFilePath... path) {
    put(p -> "", CERTIFICATE_A1_LIST, path);
  }
  
  private void load(Exec<IFilePath> add, String param, FileNameExtensionFilter filter) {
    Properties properties = new Properties();
    if (!open(properties))
      return;
    List<String> pathList = Strings.split(properties.getProperty(param, ""), LIST_DELIMITER);
    for(String path: pathList) {
      File p = Paths.get(path).toFile();
      if (p.exists() && p.isFile() && filter.accept(p)) {
        add.exec(new FilePath(p.toPath()));
      }
    }
  }
  
  @Override
  public final Optional<String> defaultAlias() {
    Properties properties = new Properties();
    if (!open(properties))
      return Optional.empty();
    return Optional.ofNullable(properties.getProperty(DEFAULT_CERTIFICATE));
  }
  
  @Override
  public final Optional<String> defaultDevice() {
    Properties properties = new Properties();
    if (!open(properties))
      return Optional.empty();
    return get(properties, 0);
  }

  @Override
  public final Optional<String> defaultCertificate() {
    Properties properties = new Properties();
    if (!open(properties))
      return Optional.empty();
    return get(properties, 1);
  }
  
  protected final boolean open(Properties properties) {
    try(FileInputStream input = new FileInputStream(config.getConfigFile())) {
      properties.load(input);
    } catch (IOException e) {
      LOGGER.warn("Não foi possível ler os arquivos de configuração", e);
      return false;
    }
    return true;
  }

  protected final void put(Function<Properties, String> start, String paramName, Object[] access) {
    Properties properties = new Properties();
    if(!open(properties))
      return;
    String output = start.apply(properties);
    for(Object sa: access) {
      if (sa == null)
        continue;
      output += (output.isEmpty() ? "" : LIST_DELIMITER) + sa.toString();
    }
    toDisk(properties, paramName, output);
  }
  
  protected final void remove(Object access, String paramName) {
    Properties properties = new Properties();
    if (!open(properties))
      return;
    String output = Strings.trim(properties.getProperty(paramName, ""));
    List<String> servers = Strings.split(output, LIST_DELIMITER);
    String accessText = access.toString();
    servers.removeIf(s -> accessText.equalsIgnoreCase(s));
    output = Strings.merge(servers, LIST_DELIMITER);
    toDisk(properties, paramName, output);
  }
  
  private void toDisk(Properties properties, String paramName, String output) {
    properties.setProperty(paramName, output);
    try(FileOutputStream out = new FileOutputStream(config.getConfigFile())) {
      properties.store(out, "Salvo em " + Dates.stringNow());
    } catch (IOException e) {
      LOGGER.warn("Não foi possível salvar o arquivo de configuração", e);
    }
  }

  private Optional<String> get(Properties properties, int index) {
    List<String> members = Strings.split(properties.getProperty(DEFAULT_CERTIFICATE, ""), ':');
    if (members.size() != 2)
      return Optional.empty();
    return Optional.of(members.get(index));
  }
}
