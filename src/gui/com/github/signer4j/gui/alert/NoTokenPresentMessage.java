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

import javax.swing.JOptionPane;

abstract class NoTokenPresentMessage {

  protected static final String MESSAGE_MAIN = "Não foi encontrado(a) um(a) certificado/chave privada operacional.\nCausas comuns:\n\n"
        + "1) Se seu certificado é do tipo A3,  tenha certeza de que o dispositivo\n"
        + "   CORRETO (token/smartcard) esteja CONECTADO no seu computador\n"
        + "   e de que esteja funcional.\n" 
        + "2) Tenha a certeza de  que o certificado digital escolhido  tenha uma \n"
        + "   chave privada associada a ele (consulte o software de administração\n"
        + "   do seu dispositivo).";

  protected static final String MESSAGE_RETRY =  "\n\nTentar identificar o certificado novamente?";

  protected static final String MESSAGE_FORMAT = MESSAGE_MAIN + MESSAGE_RETRY;
  
  protected JOptionPane jop;
}
