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


package com.github.signer4j.imp;

import java.util.List;
import java.util.Scanner;

import com.github.signer4j.ICertificates;
import com.github.signer4j.IChoice;
import com.github.signer4j.IKeyStoreAccess;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Strings;

public class ConsoleChooser extends AbstractCertificateChooser {
  
  public ConsoleChooser(IKeyStoreAccess keyStore, ICertificates certificates) {
    super(keyStore, certificates);
  }

  @Override
  protected IChoice doChoose(List<DefaultCertificateEntry> options) throws Signer4JException {
    @SuppressWarnings("resource")
    Scanner sc = new Scanner(System.in);
    do{
      System.out.println("==============================");
      System.out.println("= Choose your alias: ");
      System.out.println("==============================");

      int i = 1;
      for(DefaultCertificateEntry e: options) {
        System.out.println("[" + i++ + "] : " + e.getCertificate().getAlias() + " -> " + e.certificate.getName());
      }
      System.out.println("[" + i++ + "] : Cancell");
     
      System.out.print("Option : ");  
      String option;
      option = Strings.trim(sc.nextLine());
      
      int index;
      if ((index = Strings.toInt(option, -1)) <= 0 || --index > options.size()) {
        continue;
      }
      
      if (index == options.size()) {
        return Choice.CANCEL;
      }
      
      return toChoice(options.get(index));
      
    } while(true);
  }
}
