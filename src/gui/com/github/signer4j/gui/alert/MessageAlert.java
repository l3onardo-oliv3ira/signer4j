package com.github.signer4j.gui.alert;

import static com.github.utils4j.gui.imp.SwingTools.invokeLater;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.github.signer4j.imp.Config;
import com.github.utils4j.imp.Strings;

public final class MessageAlert {

  private static final String[] OPTIONS = {"ENTENDI"};
  
  public static void showInfo(String message) {
    invokeLater(() -> display(message));
  }
  
  public static void showFail(String message) {
    invokeLater(() -> displayFail(message));
  }
  
  public static void showInfo(String message, String textButton) {
    invokeLater(() -> display(message, textButton));
  }

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
      Strings.trim(message),
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
