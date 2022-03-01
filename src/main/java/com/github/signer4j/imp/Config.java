package com.github.signer4j.imp;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.github.signer4j.IConfigPersister;
import com.github.signer4j.IFilePath;

public class Config{
  
  private static IConfigPersister config;
  
  private static Optional<Image> icon = Optional.empty();
  
  protected static void setup(Optional<Image> image, IConfigPersister conf) {
    Config.icon = image;
    Config.config = conf != null ? conf: config;
    setupA3();
  }

  protected static IConfigPersister config() {
    return config != null ? config : (config = new ConfigPersister(new SignerConfig()));
  }

  public static Image getIcon() {
    return icon.orElse(null);
  }
  
  public static Optional<String> defaultCertificate() {
    return config().defaultCertificate();
  }

  public static Optional<String> defaultDevice() {
    return config().defaultDevice();
  }

  public static Optional<String> defaultAlias() {
    return config().defaultAlias();
  }

  public static void saveA1Paths(IFilePath... path) {
    config().saveA1Paths(path);
  }

  public static void saveA3Paths(IFilePath... path) {
    config().saveA3Paths(path);
  }

  public static void loadA1Paths(Consumer<IFilePath> add) {
    config().loadA1Paths(add);
  }

  public static void loadA3Paths(Consumer<IFilePath> add) {
    config().loadA3Paths(add);
  }

  public static void save(String defaultAlias) {
    config().save(defaultAlias);
  }
  
  public static void reset() {
    config().reset();
  }

  private static void setupA3() {
    List<IFilePath> libs = new ArrayList<>();
    loadA3Paths(libs::add);
    if (!libs.isEmpty())
      return;
    new SmartLookupStrategy().lookup(dv -> libs.add(new FilePath(dv.getLibrary())));
    saveA3Paths(libs.toArray(new FilePath[libs.size()]));
  }
  
  protected Config() {}
}

