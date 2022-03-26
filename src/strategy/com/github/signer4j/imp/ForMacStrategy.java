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

class ForMacStrategy extends PreloadedStrategy {

  //TODO revisar libraries 64bits
  
  public ForMacStrategy() {
    load("/usr/lib/libwdpkcs.dylib");
    load("/usr/local/lib/libwdpkcs.dylib"); 
    load("/usr/local/lib/libeTPkcs11.dylib");
    load("/usr/local/lib/libetpkcs11.dylib");
    load("/usr/local/lib/libaetpkss.dylib");
    load("/usr/local/lib/libbanrisulpkcs11.so");
    load("/usr/local/lib/libdesktopID_Provider.dylib");
    load("/usr/local/lib/opensc-pkcs11.so");
    load("/Applications/NeoID Desktop.app/Contents/Java/tools/macos/libneoidp11.dylib");
    load("/Applications/WatchKey USB Token Admin Tool.app/Contents/MacOS/lib/libWDP11_BR_GOV.dylib");
    load("/Applications/tokenadmin.app/Contents/Frameworks/libaetpkss.dylib");
    load("/Library/Frameworks/eToken.framework/Versions/A/libeToken.dylib");
    load("/usr/local/ngsrv/libepsng_p11.so.1.2.2");
    //firefox: load("/Applications/Firefox.app/Contents/MacOS/libsoftokn3.dylib");
    //64bits
    load("/Applications/Assistente Desktop birdID.app/Contents/resources/extraResources/osx/x64/vault-pkcs11.dylib");
  }
}
