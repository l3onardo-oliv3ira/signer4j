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

class ForLinuxStrategy extends UnixStrategy {

  //TODO revisar libraries de 64bits?
  
  public ForLinuxStrategy() {
    add("libaetpkss.so");
    add("libgpkcs11.so");
    add("libgpkcs11.so.2");
    add("libepsng_p11.so");
    add("libepsng_p11.so.1");
    add("libneoidp11.so");
    add("opensc/opensc­pkcs11.so");
    add("libeTPkcs11.so");
    add("libeToken.so");
    add("libeToken.so.4");
    add("libcmP11.so");
    add("libwdpkcs.so");
    add("libwdpkcs.so");
    add("libaetpkss.so.3");
    add("libaetpkss.so.3.0");
    add("opensc-pkcs11.so");
    add("pkcs11/opensc-pkcs11.so");
    add("libscmccid.so");
    add("watchdata/ICP/lib/libwdpkcs_icp.so");
    add("watchdata/lib/libwdpkcs.so");
    add("x86_64-linux-gnu/opensc-pkcs11.so");
    add("x86_64-linux-gnu/pkcs11/opensc-pkcs11.so");
    
    load("/usr/local/ngsrv/libepsng_p11.so.1.2.2");
    load("/usr/local/ngsrv/libepsng_p11.so.1");
    load("/usr/local/AWP/lib/libOcsCryptoki.so");
    load("/usr/lib64/libeToken.so");
    load("/usr/local/lib64/libwdpkcs.so");
    load("/opt/watchdata/lib64/libwdpkcs.so");
    load("/opt/ePass2003-Castle-20141128/i386/redist/libcastle.so.1.0.0");
    load("/opt/Assistente Desktop birdID/resources/extraResources/linux/x64/vault-pkcs11.so");
    // firefox: load("/usr/lib/nss/libsoftokn3.so");
    //64bits
    // firefox: load("/usr/lib/x86_64-linux-gnu/nss/libsoftokn3.so");
    // load("/usr/lib64/libeTPkcs11.so");
    // load("/usr/lib64/libaetpkss.so.3.0.x");
  }
}

