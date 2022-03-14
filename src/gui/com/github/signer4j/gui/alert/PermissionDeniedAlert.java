package com.github.signer4j.gui.alert;

import static com.github.utils4j.gui.imp.SwingTools.invokeLater;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.github.signer4j.gui.utils.Images;
import com.github.signer4j.imp.Config;

public final class PermissionDeniedAlert {

  public static void showInfo(String message) {
    invokeLater(() -> display(message));
  }
  
  public static boolean display(String message) {
    return new PermissionDeniedAlert(message).show();
  }
  
  private static final String[] OPTIONS = {"ENTENDI"};
  
  private final JOptionPane jop;
  
  private PermissionDeniedAlert(String message) {
    jop = new JOptionPane(
      message,
      JOptionPane.INFORMATION_MESSAGE, 
      JOptionPane.OK_OPTION, 
      Images.LOCK.asIcon().orElse(null), 
      OPTIONS, 
      OPTIONS[0]
    );
  }

  private boolean show() {
    JDialog dialog = jop.createDialog("Permissão Negada!");
    dialog.setAlwaysOnTop(true);
    dialog.setModal(true);
    dialog.setIconImage(Config.getIcon());
    dialog.setVisible(true);
    dialog.dispose();
    Object selectedValue = jop.getValue();
    return OPTIONS[0].equals(selectedValue);
  }
}
