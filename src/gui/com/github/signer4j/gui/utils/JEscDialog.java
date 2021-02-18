package com.github.signer4j.gui.utils;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public class JEscDialog extends JDialog {
  private static final long serialVersionUID = 1L;

  public JEscDialog() {
    this((Frame)null, false);
  }

  public JEscDialog(Frame owner) {
    this(owner, false);
  }

  public JEscDialog(Frame owner, boolean modal) {
    this(owner, (String)null, modal);
  }

  public JEscDialog(Frame owner, String title) {
    this(owner, title, false);
  }

  public JEscDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  public JEscDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
    super(owner, title, modal, gc);
  }

  public JEscDialog(Dialog owner) {
    this(owner, false);
  }

  public JEscDialog(Dialog owner, boolean modal) {
    this(owner, (String)null, modal);
  }

  public JEscDialog(Dialog owner, String title) {
    this(owner, title, false);
  }

  public JEscDialog(Dialog owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  public JEscDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
    super(owner, title, modal, gc);
  }

  public JEscDialog(Window owner, Dialog.ModalityType modalityType) {
    this(owner, "", modalityType);
  }

  public JEscDialog(Window owner, String title, Dialog.ModalityType modalityType) {
    super(owner, title, modalityType);
  }

  public JEscDialog(Window owner, String title, Dialog.ModalityType modalityType, GraphicsConfiguration gc) {
    super(owner, title, modalityType, gc);
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
    JEscDialog.this.setVisible(false);
    JEscDialog.this.dispose();
  }
}
