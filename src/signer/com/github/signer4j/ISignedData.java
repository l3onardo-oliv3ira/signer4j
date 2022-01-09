package com.github.signer4j;

import java.io.IOException;
import java.io.OutputStream;

public interface ISignedData extends IPersonalData {

  byte[] getSignature();

  void writeTo(OutputStream out) throws IOException;
  
  String getSignature64();
}
