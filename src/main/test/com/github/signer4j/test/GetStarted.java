package com.github.signer4j.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.github.signer4j.ICMSSigner;
import com.github.signer4j.ICertificate;
import com.github.signer4j.IDevice;
import com.github.signer4j.IDeviceManager;
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
import com.github.signer4j.imp.exception.KeyStoreAccessException;
import com.github.signer4j.imp.exception.LoginCanceledException;
import com.github.signer4j.imp.exception.NoTokenPresentException;
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

    //Access first device and listing certificates
    IDevice device = dm.firstDevice().get();

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
      //token.login("mypassword".toCharArray());
      //token.login(.....) //many other login overloaded methods
      
      String message = "Hello world!";
      
      ISimpleSigner simpleSigner = token.signerBuilder().usingAlgorigthm(SignatureAlgorithm.ASN1MD5withRSA).build();
      
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
    } catch (KeyStoreAccessException e) {
      System.out.println(e.getMessage());
    } finally {
      token.logout(); 
    }
    
    //Sign files

    try {
      token.login();

      ICMSSigner cmsSigner = token.cmsSignerBuilder()
          .usingAlgorigthm(SignatureAlgorithm.SHA1withRSA)
          .usingAttributes(true)
          .usingMemoryLimit(50 * 1024 * 1025)
          .usingSignatureType(SignatureType.ATTACHED)
          .build();
      
      ISignedData data = cmsSigner.process(new File("./input.pdf"));
      
      try(OutputStream out = new FileOutputStream(new File("./input.pdf.p7s"))) {
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
    } catch (KeyStoreAccessException e) {
      System.out.println(e.getMessage());
    } catch (IOException e) {
      System.out.println("Unabled to read input file");
    } finally {
      token.logout(); 
    }
  }
}
