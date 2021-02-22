package com.github.signer4j.gui.alert;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.github.signer4j.gui.utils.Images;
import com.github.signer4j.imp.Config;

public final class TokenUseAlert {

  private static final String MESSAGE_FORMAT = "Há uma solicitação de uso do seu "
      + "certificado digital!";
  
  private static final String[] OPTIONS = {"Ok! Fui eu mesmo que solicitei", "Não reconheço esta tentativa"};
  
  public static boolean display() {
    return new TokenUseAlert().show();
  }
  
  private final JOptionPane jop;
  
  private TokenUseAlert() {
    jop = new JOptionPane(
      MESSAGE_FORMAT,
      JOptionPane.QUESTION_MESSAGE, 
      JOptionPane.OK_OPTION, 
      Images.CERTIFICATE.asIcon(), 
      OPTIONS, 
      OPTIONS[1]
    );
  }

  private boolean show() {
    JDialog dialog = jop.createDialog("Uso do certificado");
    dialog.setAlwaysOnTop(true);
    dialog.setModal(true);
    dialog.setIconImage(Config.getIcon());
    dialog.setVisible(true);
    dialog.dispose();
    Object selectedValue = jop.getValue();
    return OPTIONS[0].equals(selectedValue);
  }
}
