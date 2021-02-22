package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;

import com.github.signer4j.ICertificates;
import com.github.signer4j.IDevice;
import com.github.signer4j.ILibraryAware;
import com.github.signer4j.IToken;
import com.github.signer4j.exception.DriverException;
import com.github.signer4j.exception.DriverFailException;

class PKCS12Slot extends AbstractSlot implements ILibraryAware {

  private PKCS12Token token;
  
  private final Path certPath;
  
  PKCS12Slot(Path certPath) throws DriverException {
    super(-1);
    this.certPath = requireNonNull(certPath, "cert path can't be null");
    this.setup();
  }
  
  @Override
  public final String getDescription() {
    return "Virtual PKCS12 Slot";
  }

  @Override
  public final String getManufacturerId() {
    return "https://github.com/l3onardo-oliv3ira";
  }

  @Override
  public final String getHardwareVersion() {
    return "Universal";
  }

  @Override
  public final String getFirmewareVersion() {
    return "1.0";
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
