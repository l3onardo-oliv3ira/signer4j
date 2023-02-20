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

import com.github.utils4j.imp.Jvms;

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
    load(winRoot.concat("/System32/WDICP_P11_CCID_v34.dll"));
    load(winRoot.concat("/System32/desktopID_Provider.dll"));
    load(winRoot.concat("/System32/DXSafePKCS11.dll"));
    //Gemalto x86
    load("C:/Program Files (x86)/Oberthur Technologies/AWP/DLLs/OcsCryptolib_P11.dll");
    load("C:/Program Files (x86)/Gemalto/IDGo 800 PKCS11/IDPrimePKCS11.dll");
    load("C:/Program Files (x86)/Oberthur Technologies/AWP/DLLs/OcsCryptoki.dll");
    load("C:/Program Files (x86)/CSP Banrisul Multiplo/PKCS11.dll");
    //64BITS:
    load("C:/Program Files/Gemplus/GemSafe Libraries/BIN/gclib.dll");
    load("C:/Program Files/Gemalto/IDGo 800 PKCS11/IDPrimePKCS1164.dll");
    load("C:/Program Files/Assistente Desktop birdID/resources/extraResources/windows/x64/vault-pkcs11.dll");
    load("C:/Program Files/Oberthur Technologies/AuthentIC Webpack/DLLs/OCSCryptoki.dll");
    load("C:/Program Files/CSP Banrisul Multiplo/PKCS11.dll");
    //load(winRoot.concat("/SysWOW64/aetpkss1.dll")); //certsign
    load(winRoot.concat("/SysWOW64/opensc-pkcs11.dll"));
    load(winRoot.concat("/SysWOW64/WDICP_P11_CCID_v34.dll"));
  }
}
