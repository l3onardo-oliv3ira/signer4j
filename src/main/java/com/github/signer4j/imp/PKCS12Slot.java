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

import java.io.IOException;
import java.nio.file.Path;

import com.github.signer4j.ICertificates;
import com.github.signer4j.IDevice;
import com.github.signer4j.ILibraryAware;
import com.github.signer4j.IToken;
import com.github.signer4j.exception.DriverException;
import com.github.signer4j.exception.DriverFailException;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Streams;

class PKCS12Slot extends VirtualSlot implements ILibraryAware {

  private PKCS12Token token;
  
  private final Path certPath;
  
  PKCS12Slot(Path certPath) throws DriverException {
    super(-1, "Pkcs12");
    this.certPath = Args.requireNonNull(certPath, "cert path can't be null");
    this.setup();
  }
  
  @Override
  public final String getLibrary() {
    return certPath.toFile().getAbsolutePath();
  }
  
  @Override
  public final IToken getToken() {
    return token;
  }
  
  private void setup() throws DriverException {
    String md5;
    try {
      md5 = Streams.checkMd5Sum(certPath.toFile());
    } catch (IOException e) {
      throw new DriverFailException("can't access " + certPath, e);
    }
    this.token = new PKCS12Token.Builder(this)
      .withLabel(certPath.getFileName().toString())
      .withModel(IDevice.Type.A1.toString())
      .withSerial(md5)
      .withManufacture(certPath.getParent().toString())
      .build();
    
    this.device = new DefaultDevice.Builder(IDevice.Type.A1)
        .withDriver(this.getLibrary())
        .withSlot(this)
        .withLabel(this.token.getLabel())
        .withModel(this.token.getModel())
        .withSerial(this.token.getSerial())
        .withCertificates(this.token.getCertificates())
        .build();
  }
  
  void updateDevice(ICertificates certificates) {
    this.device.setCertificates(certificates);
  }
}
