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


package com.github.signer4j.gui.alert;

import static com.github.utils4j.gui.imp.SwingTools.invokeAndWait;
import static com.github.utils4j.gui.imp.SwingTools.invokeLater;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.github.signer4j.gui.utils.Images;
import com.github.signer4j.imp.Config;

public final class TokenLockedAlert {

  private static final String MESSAGE_FORMAT = "Infelizmente seu dispositivo encontra-se BLOQUEADO.\n\n"
      + "A única forma de desbloqueá-lo é fazendo uso da sua senha de administração,\n"
      + "também conhecida como PUK.\n\n"
      + "Se você desconhece sua senha PUK, seu certificado foi perdido para sempre\n"
      + "e um novo certificado deverá ser providenciado.";
  
  private static final String[] OPTIONS = {"ENTENDI"};
  
  public static boolean display() {
    return new TokenLockedAlert().reveal();
  }
  
  public static void show() { 
    invokeLater(TokenLockedAlert::display);
  }
  
  public static void showAndWait() {
    invokeAndWait(TokenLockedAlert::display);
  }
  
  private final JOptionPane jop;
  
  private TokenLockedAlert() {
    jop = new JOptionPane(
      MESSAGE_FORMAT,
      JOptionPane.INFORMATION_MESSAGE, 
      JOptionPane.OK_OPTION, 
      Images.LOCK.asIcon(), 
      OPTIONS, 
      OPTIONS[0]
    );
  }

  private boolean reveal() {
    JDialog dialog = jop.createDialog("Dispositivo bloqueado");
    dialog.setAlwaysOnTop(true);
    dialog.setModal(true);
    dialog.setIconImage(Config.getIcon());
    dialog.setVisible(true);
    dialog.dispose();
    Object selectedValue = jop.getValue();
    return OPTIONS[0].equals(selectedValue);
  }
}
