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
    load("/Applications/NeoID Desktop.app/Contents/Java/tools/macos/libneoidp11.dylib");
    load("/Applications/WatchKey USB Token Admin Tool.app/Contents/MacOS/lib/libWDP11_BR_GOV.dylib");
    load("/Applications/tokenadmin.app/Contents/Frameworks/libaetpkss.dylib");
    load("/Library/Frameworks/eToken.framework/Versions/A/libeToken.dylib");
  }
}
