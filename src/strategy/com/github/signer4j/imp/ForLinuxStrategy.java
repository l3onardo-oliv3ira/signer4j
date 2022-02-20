package com.github.signer4j.imp;

class ForLinuxStrategy extends PreloadedStrategy {

  //TODO revisar libraries de 64bits?
  
  public ForLinuxStrategy() {
    load("/usr/lib/libaetpkss.so");
    load("/usr/lib/libgpkcs11.so");
    load("/usr/lib/libgpkcs11.so.2");
    load("/usr/lib/libepsng_p11.so");
    load("/usr/lib/libepsng_p11.so.1");
    load("/usr/local/ngsrv/libepsng_p11.so.1");
    load("/usr/lib/libeTPkcs11.so");
    load("/usr/lib/libeToken.so");
    load("/usr/lib/libeToken.so.4");
    load("/usr/lib/libcmP11.so");
    load("/usr/lib/libwdpkcs.so");
    load("/usr/local/lib/libwdpkcs.so");
    load("/usr/lib/watchdata/ICP/lib/libwdpkcs_icp.so");
    load("/usr/lib/watchdata/lib/libwdpkcs.so");
    load("/usr/lib/libaetpkss.so.3");
    load("/usr/lib/libaetpkss.so.3.0");
    load("/usr/lib/opensc-pkcs11.so");
    load("/usr/lib/pkcs11/opensc-pkcs11.so");
    load("/usr/local/ngsrv/libepsng_p11.so.1.2.2");
    load("/usr/local/AWP/lib/libOcsCryptoki.so");
    load("/usr/lib/libscmccid.so");
    load("/opt/ePass2003-Castle-20141128/i386/redist/libcastle.so.1.0.0");
    load("/usr/lib/libneoidp11.so");
    load("/usr/lib/opensc/opensc­pkcs11.so");
    // firefox: load("/usr/lib/nss/libsoftokn3.so");
    //64bits
    // firefox: load("/usr/lib/x86_64-linux-gnu/nss/libsoftokn3.so");
    load("/usr/lib/x86_64-linux-gnu/opensc-pkcs11.so");
    load("/usr/lib/x86_64-linux-gnu/pkcs11/opensc-pkcs11.so");
    load("/usr/lib/x86_64-linux-gnu/pkcs11/opensc-pkcs11.so");
    load("/opt/Assistente Desktop birdID/resources/extraResources/linux/x64/vault-pkcs11.so");
    load("/usr/lib64/libeToken.so");
    load("/usr/local/lib64/libwdpkcs.so");
    load("/opt/watchdata/lib64/libwdpkcs.so");
  }
}

