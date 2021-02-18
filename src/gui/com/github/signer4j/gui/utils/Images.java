package com.github.signer4j.gui.utils;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;

import javax.swing.ImageIcon;

public enum Images {
  LOCK("/lock.png"),
  
  CERTIFICATE("/certificate.png");

  final String path;
  
  Images(String path) {
    this.path = path;
  }
  
  public InputStream asStream() {
    return getClass().getResourceAsStream(path);
  }
  
  public Image asImage() {
    return Toolkit.getDefaultToolkit().createImage(getClass().getResource(path));
  }

  public ImageIcon asIcon() {
    return new ImageIcon(getClass().getResource(path));
  }
}


