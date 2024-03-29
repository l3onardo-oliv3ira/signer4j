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


package com.github.signer4j.gui.utils;

import static java.lang.String.format;

import java.awt.Image;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public final class InvalidPinAlert {

  private static final String MESSAGE_FORMAT = "Senha inválida! Tentar novamente?";
  
  private static final String MESSAGE_FORMAT_ALERT = "Senha inválida!\n" +
      "ATENÇÃO: Você já  errou a  senha por %s vezes!\n" +
      "Por segurança o excesso de tentativas erradas\n" +
      "\n=>BLOQUEARÁ O SEU DISPOSITIVO<=.\n\n" +
      "Se você não possui uma senha de administração (PUK), procure um\n" +
      "suporte técnico *ANTES* que seu dispositivo seja bloqueado!\n\n" +
      "Deseja tentar novamente? (Na dúvida, opte por NÃO)";

  private static final String[] OPTIONS = {"SIM", "NÃO"};
  
  public static boolean display() {
    return display(0);
  }
  
  public static boolean display(int times) {
    return display(times, Images.LOCK.asImage());
  }

  public static boolean display(int times, Image icon) {
    return new InvalidPinAlert(times, icon).show();
  }
  
  private final JOptionPane jop;

  private final Image icon;
  
  private InvalidPinAlert(int times, Image icon) {
    this.icon = icon;
    this.jop = new JOptionPane(
      format(times < 2 ? MESSAGE_FORMAT : MESSAGE_FORMAT_ALERT, times),
      JOptionPane.QUESTION_MESSAGE, 
      JOptionPane.YES_NO_OPTION, 
      Images.LOCK.asIcon(), 
      OPTIONS, 
      OPTIONS[1]
    );
  }

  private boolean show() {
    JDialog dialog = jop.createDialog("Alerta de seguraça");
    dialog.setAlwaysOnTop(true);
    dialog.setModal(true);
    dialog.setIconImage(icon);
    dialog.setVisible(true);
    dialog.dispose();
    Object selectedValue = jop.getValue();
    return OPTIONS[0].equals(selectedValue);
  }
  
  public static void main(String[] args) {
    InvalidPinAlert.display(2);
  }
}
