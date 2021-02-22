package com.github.signer4j.imp;

import java.awt.Image;

import com.github.signer4j.IConfigPersister;
import com.github.signer4j.gui.utils.Images;

public final class Config{
  
  private static IConfigPersister CONF;
  
  private static Image ICON;

  private Config() {}

  public static Image getIcon() {
    if (ICON == null)
      ICON = Images.KEY.asImage();
    return ICON;
  }

  public static IConfigPersister persister() {
    if (CONF == null)
      CONF = new ConfigPersister(new SignerConfig());
    return CONF;
  }
  
  public static void setup(Image icon, IConfigPersister config) {
    if (icon != null)
      Config.ICON = icon;
    if (config != null)
      Config.CONF = config;
  }
}
