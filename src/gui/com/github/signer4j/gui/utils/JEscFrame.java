package com.github.signer4j.gui.utils;

import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public class JEscFrame extends JFrame {

  private static final long serialVersionUID = 1L;

  public JEscFrame() {}

  public JEscFrame(GraphicsConfiguration gc) {
    super(gc);
  }

  public JEscFrame(String title) {
    super(title);
  }

  public JEscFrame(String title, GraphicsConfiguration gc) {
    super(title, gc);
  }

  protected JRootPane createRootPane() {
    JRootPane rootPane = new JRootPane();
    KeyStroke stroke = KeyStroke.getKeyStroke(27, 0);
    rootPane.getInputMap(2).put(stroke, "escapeKey");
    rootPane.getActionMap().put("escapeKey", new AbstractAction() {
      private static final long serialVersionUID = 1L;
      public void actionPerformed(ActionEvent e) {
        onEscPressed(e);
      }
    });
    return rootPane;
  }
  
  protected void onEscPressed(ActionEvent e) {
    this.close();
  }

  protected void close() {
    this.setVisible(false);
    this.dispose();
  }
}