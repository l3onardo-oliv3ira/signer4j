package com.github.signer4j.gui.alert;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.github.signer4j.imp.Config;

public final class MessageAlert {

  private static final String[] OPTIONS = {"ENTENDI"};
  
  public static boolean display(String message) {
    return new MessageAlert(message, OPTIONS, JOptionPane.INFORMATION_MESSAGE).show();
  }
  
  public static boolean display(String message, String textButton) {
    return new MessageAlert(message, new String[] { textButton }, JOptionPane.INFORMATION_MESSAGE).show();
  }

  public static boolean displayFail(String message) {
    return new MessageAlert(message, OPTIONS, JOptionPane.ERROR_MESSAGE).show();
  }
  
  public static boolean displayFail(String message, String textButton) {
    return new MessageAlert(message, new String[] { textButton }, JOptionPane.ERROR_MESSAGE).show();
  }
  
  public static boolean display(String message, String textButton, int optionPane) {
    return new MessageAlert(message, new String[] { textButton }, optionPane).show();
  }
  
  private final JOptionPane jop;
  
  private final String[] options;
  
  private MessageAlert(String message) {
    this(message, JOptionPane.OK_OPTION);
  }
  
  private MessageAlert(String message, int optionPane) {
    this(message, OPTIONS, optionPane);
  }

  private MessageAlert(String message, String[] options, int optionPane) {
    jop = new JOptionPane(
      message,
      optionPane, 
      JOptionPane.OK_OPTION, 
      null, 
      this.options = options, 
      options[0]
    );
  }

  private boolean show() {
    JDialog dialog = jop.createDialog("Atenção!");
    dialog.setAlwaysOnTop(true);
    dialog.setModal(true);
    dialog.setIconImage(Config.getIcon());
    dialog.setVisible(true);
    dialog.toFront();
    dialog.dispose();
    Object selectedValue = jop.getValue();
    return options[0].equals(selectedValue);
  }
}
