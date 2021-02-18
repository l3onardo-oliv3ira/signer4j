package com.github.signer4j.imp;

class ForWindowsStrategy extends PreloadedStrategy {

  public ForWindowsStrategy() {
    final String winRoot = Jvms.SYSTEM_ROOT;
    load(winRoot.concat("/System32/ep2pk11.dll"));
    load(winRoot.concat("/System32/ngp11v211.dll"));
    load(winRoot.concat("/System32/aetpkss1.dll"));
    load(winRoot.concat("/System32/gclib.dll"));
    load(winRoot.concat("/System32/pk2priv.dll"));
    load(winRoot.concat("/System32/w32pk2ig.dll"));
    load(winRoot.concat("/System32/eTPkcs11.dll"));
    load(winRoot.concat("/System32/acospkcs11.dll"));
    load(winRoot.concat("/System32/dkck201.dll"));
    load(winRoot.concat("/System32/dkck232.dll"));
    load(winRoot.concat("/System32/cryptoki22.dll"));
    load(winRoot.concat("/System32/acpkcs.dll"));
    load(winRoot.concat("/System32/slbck.dll"));
    load(winRoot.concat("/System32/cmP11.dll"));
    load(winRoot.concat("/System32/WDPKCS.dll"));
    load(winRoot.concat("/System32/Watchdata/Watchdata Brazil CSP v1.0/WDPKCS.dll"));
    load(winRoot.concat("/System32/SerproPkcs11.dll"));
    load(winRoot.concat("/System32/OcsCryptoki.dll"));
    //icpbravo libraries!
    load(winRoot.concat("/System32/opensc-pkcs11.dll"));
    load(winRoot.concat("/System32/axaltocm.dll"));
    load(winRoot.concat("/System32/usbr38.dll"));
    load(winRoot.concat("/System32/cs2_pkcs11.dll"));
    load(winRoot.concat("/System32/CccSigIT.dll"));
    load(winRoot.concat("/System32/dspkcs.dll"));
    load(winRoot.concat("/System32/SetTokI.dll"));
    load(winRoot.concat("/System32/psepkcs11.dll"));
    load(winRoot.concat("/System32/id2cbox.dll"));
    load(winRoot.concat("/System32/smartp11.dll"));
    load(winRoot.concat("/System32/pkcs201n.dll"));
    load(winRoot.concat("/System32/cryptoki.dll"));
    load(winRoot.concat("/System32/AuCryptoki2-0.dll"));
    load(winRoot.concat("/System32/cknfast.dll"));
    load(winRoot.concat("/System32/cryst201.dll"));
    load(winRoot.concat("/System32/softokn3.dll"));
    load(winRoot.concat("/System32/iveacryptoki.dll"));
    load(winRoot.concat("/System32/sadaptor.dll"));
    load(winRoot.concat("/System32/pkcs11.dll"));
    load(winRoot.concat("/System32/siecap11.dll"));
    load(winRoot.concat("/System32/asepkcs.dll"));
    //Gemalto x86
    load("C:/Program Files (x86)/Gemalto/IDGo 800 PKCS11/IDPrimePKCS11.dll");
    
    //64BITS: 
    /*
     load("C:/Program Files/Gemalto/IDGo 800 PKCS11/IDPrimePKCS1164.dll");
     load("C:/Program Files/Oberthur Technologies/AuthentIC Webpack/DLLs/OCSCryptoki.dll");
     load("C:/Program Files/CSP Banrisul Multiplo/PKCS11.dll");
     load("C:/Windows/SysWOW64/aetpkss1.dll"); //certsign
    */
    load("C:/Program Files (x86)/Oberthur Technologies/AWP/DLLs/OcsCryptoki.dll");
    load("C:/Program Files (x86)/CSP Banrisul Multiplo/PKCS11.dll");
    load("C:/Arquivos de programas/Gemplus/GemSafe Libraries/BIN/gclib.dll");
    load("C:/Program Files/Gemplus/GemSafe Libraries/BIN/gclib.dll");
  }
}
