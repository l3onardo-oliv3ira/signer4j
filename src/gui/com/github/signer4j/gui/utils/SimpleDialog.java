package com.github.signer4j.gui.utils;

import java.awt.Frame;

import com.github.signer4j.imp.Config;

public class SimpleDialog extends JEscDialog {
  
  private static final long serialVersionUID = 1L;

  public SimpleDialog(String title) {
    this((Frame)null, title);
  }
  
  public SimpleDialog(String title, boolean modal) {
    this((Frame)null, title, modal);
  }
  
  public SimpleDialog(Frame owner, String title) {
    this(owner, title, false);
  }

  public SimpleDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    this.setupIcon();
  }

  private void setupIcon() {
    this.setIconImage(Config.getIcon());
  }
  
  public void close() {
    this.setVisible(false);
    this.dispose();
  }
}
