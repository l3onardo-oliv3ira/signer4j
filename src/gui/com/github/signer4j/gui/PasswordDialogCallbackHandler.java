package com.github.signer4j.gui;

import static com.github.signer4j.imp.Args.requireNonNull;
import static com.github.signer4j.imp.GuiTools.mouseTracker;
import static com.github.signer4j.imp.Strings.trim;
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
import com.github.signer4j.imp.GuiTools;
import com.github.signer4j.imp.ResponseCallback;

public class PasswordDialogCallbackHandler implements IPasswordCallbackHandler {

  private static final String DEFAULT_PIN_TITLE = "Informe a senha";

  private final String title;

  private final IGadget gadget;
  
  private final IPasswordCollector collector;
  
  public PasswordDialogCallbackHandler(IGadget gadget) {
    this(gadget, DEFAULT_PIN_TITLE, p -> {});
  }

  public PasswordDialogCallbackHandler(IGadget gadget, IPasswordCollector collector) {
    this(gadget, DEFAULT_PIN_TITLE, collector);
  }

  public PasswordDialogCallbackHandler(IGadget gadget, String title, IPasswordCollector collector) {
    this.title = trim(title, DEFAULT_PIN_TITLE);
    this.gadget = requireNonNull(gadget, "token is null");
    this.collector = requireNonNull(collector, "collector is null");
  }

  @Override
  public ResponseCallback doHandle(final PasswordCallback callback) {
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
    dialog.setIconImage(Config.getIcon());
    dialog.setAlwaysOnTop(true);
    dialog.addComponentListener(new ComponentAdapter() {
      public void componentShown(ComponentEvent e) {
        passwordField.requestFocusInWindow();
      }
    });
    
    try {
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
