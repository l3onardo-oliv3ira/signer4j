/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package com.github.signer4j.gui;

import static com.github.utils4j.imp.GuiTools.mouseTracker;
import static com.github.utils4j.imp.Strings.trim;
import static java.lang.String.format;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.security.auth.callback.PasswordCallback;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import com.github.signer4j.IGadget;
import com.github.signer4j.IPasswordCallbackHandler;
import com.github.signer4j.IPasswordCollector;
import com.github.signer4j.imp.Config;
import com.github.signer4j.imp.ResponseCallback;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.GuiTools;

public class PasswordDialogCallbackHandler implements IPasswordCallbackHandler {

  private static final String DEFAULT_PIN_TITLE = "Informe a senha";

  private final String title;

  private final IGadget gadget;
  
  private final IPasswordCollector collector;
  
  public PasswordDialogCallbackHandler(IGadget gadget) {
    this(gadget, DEFAULT_PIN_TITLE, IPasswordCollector.NOTHING);
  }

  public PasswordDialogCallbackHandler(IGadget gadget, IPasswordCollector collector) {
    this(gadget, DEFAULT_PIN_TITLE, collector);
  }

  public PasswordDialogCallbackHandler(IGadget gadget, String title, IPasswordCollector collector) {
    this.title = trim(title, DEFAULT_PIN_TITLE);
    this.gadget = Args.requireNonNull(gadget, "token is null");
    this.collector = Args.requireNonNull(collector, "collector is null");
  }

  @Override
  public ResponseCallback doHandle(final PasswordCallback callback) {
    if (Thread.currentThread().isInterrupted())
      return ResponseCallback.CANCEL;
    JPasswordField passwordField = new JPasswordField();
    JComponent[] components = new JComponent[5];
    components[0] = new JLabel(format("Token: %s - Modelo: %s", 
      gadget.getLabel(), 
      gadget.getModel()
    ));
    components[1] = new JLabel(format("Fabricante: %s ", gadget.getManufacturer()));
    components[2] = new JLabel("Número de série: " + gadget.getSerial());
    components[3] = new JLabel("Senha/PIN: ");
    components[4] = passwordField;
    JOptionPane panel = new JOptionPane(components,
      JOptionPane.QUESTION_MESSAGE, 
      JOptionPane.OK_CANCEL_OPTION
    );
    JDialog dialog = panel.createDialog(title);
    try {
      dialog.setIconImage(Config.getIcon());
      dialog.setAlwaysOnTop(true);
      dialog.addComponentListener(new ComponentAdapter() {
        public void componentShown(ComponentEvent e) {
          passwordField.requestFocusInWindow();
        }
      });
      
      mouseTracker(dialog);
      for(final Integer ok = JOptionPane.OK_OPTION;;) {
        GuiTools.showOnMousePointer(dialog);
        if (!ok.equals(panel.getValue()))
          return ResponseCallback.CANCEL;
        char[] password = passwordField.getPassword();
        if (password.length > 0) {
          callback.setPassword(password);
          collector.collect(password);
          return ResponseCallback.OK;
        }
      }
    }finally {
      dialog.dispose();
    }
  }
}
