package com.github.signer4j.gui.alert;

import static com.github.utils4j.gui.imp.SwingTools.invokeAndWait;
import static com.github.utils4j.gui.imp.SwingTools.invokeLater;

import java.awt.Image;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.github.utils4j.imp.Strings;

public class NoTokenPresentInfo extends NoTokenPresentMessage {
  
  public static AtomicBoolean VISIBLE = new AtomicBoolean(false);

  private static final String[] OPTIONS_OK = {"ENTENDI"};
  
  public static void showInfoOnly() {
    invokeLater(() -> displayOnly(MESSAGE_MAIN));
  }
  
  public static void showInfoOnlyAndWait() {
    invokeAndWait(() -> displayOnly(MESSAGE_MAIN));
  }
  
  private static boolean displayOnly(String message) {
    if (!VISIBLE.getAndSet(true)) {
      return new NoTokenPresentInfo(message, OPTIONS_OK, JOptionPane.INFORMATION_MESSAGE).show(null);
    }
    return false;
  }

  private String[] options;
  
  private NoTokenPresentInfo(String message) {
    this(message, JOptionPane.OK_OPTION);
  }
  
  private NoTokenPresentInfo(String message, int optionPane) {
    this(message, OPTIONS_OK, optionPane);
  }

  private NoTokenPresentInfo(String message, String[] options, int optionPane) {
    jop = new JOptionPane(
      Strings.trim(message),
      optionPane, 
      JOptionPane.OK_OPTION, 
      null, 
      this.options = options, 
      options[0]
    );
  }

  private boolean show(Image icon) {
    JDialog dialog = jop.createDialog("Atenção!");
    dialog.setAlwaysOnTop(true);
    dialog.setModal(true);
    dialog.setIconImage(icon);
    dialog.setVisible(true);
    dialog.dispose();
    VISIBLE.set(false);
    Object selectedValue = jop.getValue();
    return options[0].equals(selectedValue);
  }
  
  public static void main(String[] args) {
    NoTokenPresentInfo.showInfoOnly();
  }
}
