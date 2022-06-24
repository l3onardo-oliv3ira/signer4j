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


package com.github.signer4j.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.function.Predicate;

import com.github.signer4j.ICMSSigner;
import com.github.signer4j.ICertificate;
import com.github.signer4j.IDevice;
import com.github.signer4j.IDeviceManager;
import com.github.signer4j.IPKCS7Signer;
import com.github.signer4j.ISignedData;
import com.github.signer4j.ISimpleSigner;
import com.github.signer4j.ISlot;
import com.github.signer4j.IToken;
import com.github.signer4j.cert.ICertificatePF;
import com.github.signer4j.cert.ICertificatePJ;
import com.github.signer4j.imp.DeviceManager;
import com.github.signer4j.imp.SignatureAlgorithm;
import com.github.signer4j.imp.SignatureType;
import com.github.signer4j.imp.exception.InvalidPinException;
import com.github.signer4j.imp.exception.LoginCanceledException;
import com.github.signer4j.imp.exception.NoTokenPresentException;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.signer4j.imp.exception.TokenLockedException;

public class GetStarted {

  public static void main(String[] args) {

    IDeviceManager dm = new DeviceManager();
    //Listing all detected devices

    System.out.println("**************************");
    System.out.println("Device info");
    dm.getDevices().stream().forEach(d -> {
      IDevice device = d;
      System.out.println("Driver: " + device.getDriver());
      System.out.println("Label: " + device.getLabel());
      System.out.println("Model: " + device.getModel());
      System.out.println("Serial: " + device.getSerial());
    });
    
    Predicate<IDevice> filter = d -> true;// d.getLabel().equals("whatever");

    //Access first device and listing certificates
    Optional<IDevice> dev = dm.firstDevice(filter);
    if (!dev.isPresent())
      return;
      
    IDevice device = dev.get();

    System.out.println("**************************");
    System.out.println("Certificates");
    device.getCertificates().stream().forEach(c -> {
      ICertificate certificate = c;
      System.out.println("---------");
      System.out.println("Name: " + certificate.getName());
      System.out.println("After date: " + certificate.getAfterDate());
      System.out.println("Before date: " + certificate.getBeforeDate());
      System.out.println("Email: " + certificate.getEmail().orElse("Unknown"));
      //.... c.get*
      if (certificate.hasCertificatePF()) {
        ICertificatePF pf = certificate.getCertificatePF().get();
        System.out.println("CPF: " + pf.getCPF().get());
        //.... pf.get*
      }
      if (certificate.hasCertificatePJ()) {
        ICertificatePJ pj = certificate.getCertificatePJ().get();
        System.out.println("CNPJ: " + pj.getCNPJ().get());
        //.... pj.get*
      }
    });

    //Show slot info for each device
    ISlot slot = device.getSlot();

    System.out.println("**************************");
    System.out.println("Slot information");
    System.out.println("Description: " + slot.getDescription());
    System.out.println("FirmewareVersion: " + slot.getFirmewareVersion());
    System.out.println("HardwareVersion: " + slot.getHardwareVersion());
    System.out.println("ManufacturerId: " + slot.getManufacturer());
    System.out.println("Number: " + slot.getNumber());
    System.out.println("Serial: " + slot.getSerial());
    
    //Get token abstraction for a slot
    IToken token = slot.getToken();
    
    try {
      token.login();
      
      String message = "Hello world!";
      
      ISimpleSigner simpleSigner = token.signerBuilder().usingAlgorithm(SignatureAlgorithm.SHA1withRSA).build();
      
      ISignedData data = simpleSigner.process(message);
      
      System.out.println("base64 signed data" + data.getSignature64());
      
    } catch (TokenLockedException e) {
      System.out.println("Your token is blocked");
    } catch (InvalidPinException e) {
      System.out.println("Your password is incorrect!");
    } catch (NoTokenPresentException e) {
      System.out.println("Your token is not connected to USB");
    } catch (LoginCanceledException e) {
      System.out.println("Authentication canceled by user");
    } catch (Signer4JException e) {
      System.out.println(e.getMessage());
    } finally {
      token.logout(); 
    }
    
    //Sign files

    try {
      token.login();

      ICMSSigner cmsSigner = token.cmsSignerBuilder()
          .usingSignatureAlgorithm(SignatureAlgorithm.SHA1withRSA)
          .usingAttributes(true)
          .usingMemoryLimit(50 * 1024 * 1025)
          .usingSignatureType(SignatureType.ATTACHED)
          .build();
      
      ISignedData data = cmsSigner.process(new File("./input.pdf"));
      
      try(OutputStream out = new FileOutputStream(new File("D:/input.pdf.p7s"))) {
        data.writeTo(out);
      }
    } catch (TokenLockedException e) {
      System.out.println("Your token is blocked");
    } catch (InvalidPinException e) {
      System.out.println("Your password is incorrect!");
    } catch (NoTokenPresentException e) {
      System.out.println("Your token is not connected to USB");
    } catch (LoginCanceledException e) {
      System.out.println("Authentication canceled by user");
    } catch (Signer4JException e) {
      System.out.println(e.getMessage());
    } catch (IOException e) {
      System.out.println("Unabled to read input file");
    } finally {
      token.logout(); 
    }
    
    
    try {
      token.login();

      IPKCS7Signer pkcs7 = token.pkcs7SignerBuilder()
          .usingChallengePassword("12345")
          .usingEmailAddress("teste@gmail.com", "teste02@gmail.com")
          .usingSignatureAlgorithm(SignatureAlgorithm.SHA1withRSA)
          .usingSignatureType(SignatureType.ATTACHED)
          .usingUnstructuredAddress("address01", "address02")
          .usingUnstructuredName("xpto1", "xpto2")
          .build();
      
      ISignedData data = pkcs7.process(new File("./input.pdf"));
      
      try(OutputStream out = new FileOutputStream(new File("D:/input.pdf.2.p7s"))) {
        data.writeTo(out);
      }
    } catch (TokenLockedException e) {
      System.out.println("Your token is blocked");
    } catch (InvalidPinException e) {
      System.out.println("Your password is incorrect!");
    } catch (NoTokenPresentException e) {
      System.out.println("Your token is not connected to USB");
    } catch (LoginCanceledException e) {
      System.out.println("Authentication canceled by user");
    } catch (Signer4JException e) {
      System.out.println(e.getMessage());
    } catch (IOException e) {
      System.out.println("Unabled to read input file");
    } finally {
      token.logout(); 
    }
    dm.close();
  }
 
}
