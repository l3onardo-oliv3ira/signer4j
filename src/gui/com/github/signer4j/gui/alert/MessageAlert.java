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
