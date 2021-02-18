package com.github.signer4j.imp;

import java.util.List;
import java.util.Scanner;

import com.github.signer4j.ICertificates;
import com.github.signer4j.IKeyStoreAccess;
import com.github.signer4j.imp.exception.KeyStoreAccessException;

public class ConsoleChooser extends AbstractCertificateChooser {
  
  public ConsoleChooser(IKeyStoreAccess keyStore, ICertificates certificates) {
    super(keyStore, certificates);
  }

  @Override
  protected IChoice doChoose(List<CertificateEntry> options) throws KeyStoreAccessException {
    @SuppressWarnings("resource")
    Scanner sc = new Scanner(System.in);
    do{
      System.out.println("==============================");
      System.out.println("= Choose your alias: ");
      System.out.println("==============================");

      int i = 1;
      for(CertificateEntry e: options) {
        System.out.println("[" + i++ + "] : " + e.aliasName + " -> " + e.certificate.getName());
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
