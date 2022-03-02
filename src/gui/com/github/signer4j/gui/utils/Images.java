package com.github.signer4j.gui.utils;

import com.github.utils4j.gui.IPicture;

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
  public String path() {
    return path;
  }
}


