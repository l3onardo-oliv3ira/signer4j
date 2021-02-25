package com.github.signer4j.gui.utils;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;

import javax.swing.ImageIcon;

public enum Images implements IPicture {
  
  ICON_A1("/a1.png"),

  ICON_A3("/a3.png"),

  LOCK("/lock.png"),

  KEY("/key.png"),
  
  REFRESH("/update.png"),
  
  CERTIFICATE("/certificate.png");

  final String path;
  
  Images(String path) {
    this.path = path;
  }
  
  @Override
  public InputStream asStream() {
    return getClass().getResourceAsStream(path);
  }
  
  @Override
  public Image asImage() {
    return Toolkit.getDefaultToolkit().createImage(getClass().getResource(path));
  }

  @Override
  public ImageIcon asIcon() {
    return new ImageIcon(getClass().getResource(path));
  }
}


