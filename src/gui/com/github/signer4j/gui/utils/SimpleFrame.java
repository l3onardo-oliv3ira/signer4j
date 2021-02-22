package com.github.signer4j.gui.utils;

import java.awt.GraphicsConfiguration;

import com.github.signer4j.imp.Config;

public class SimpleFrame extends JEscFrame {
  private static final long serialVersionUID = 1L;

  public SimpleFrame(String title) {
    super(title);
    setupIcon();
  }

  public SimpleFrame(String title, GraphicsConfiguration gc) {
    super(title, gc);
    setupIcon();
  }

  private void setupIcon() {
    this.setIconImage(Config.getIcon());
  }
  
  public void showToFront(){
    this.setVisible(true); 
    this.toFront();
  }
  
  public void close() {
    this.setVisible(false);
    this.dispose();
  }
}
