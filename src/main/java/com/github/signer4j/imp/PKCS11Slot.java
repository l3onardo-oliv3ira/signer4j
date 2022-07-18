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

import com.github.signer4j.IDevice;
import com.github.signer4j.ILibraryAware;
import com.github.signer4j.IToken;
import com.github.signer4j.exception.DriverException;
import com.github.signer4j.exception.DriverFailException;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Objects;

import sun.security.pkcs11.wrapper.CK_SLOT_INFO;
import sun.security.pkcs11.wrapper.CK_TOKEN_INFO;
import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.pkcs11.wrapper.PKCS11Exception;

@SuppressWarnings("restriction")
class PKCS11Slot extends AbstractSlot implements ILibraryAware {

  private static final String UNKNOWN_INFORMATION = "Unknown";
  
  protected PKCS11Token token;
  
  private String description;
  
  private String manufacturerId;
  
  private String firmwareVersion;
  
  private String hardwareVersion;
  
  private final transient PKCS11Driver driver;
  
  PKCS11Slot(PKCS11Driver driver, long number) throws DriverException {
    super(number);
    this.driver = Args.requireNonNull(driver, "Unabled to create slot with null driver instance");
    this.setup();
  }
  
  final PKCS11 getPK() {
    return this.driver.getPK();
  }

  @Override
  public final String getDescription() {
    return this.description;
  }

  @Override
  public final String getManufacturer() {
    return this.manufacturerId;
  }

  @Override
  public final String getHardwareVersion() {
    return this.hardwareVersion;
  }

  @Override
  public final String getFirmewareVersion() {
    return this.firmwareVersion;
  }
  
  @Override
  public final IToken getToken() {
    return token;
  }
  
  @Override
  public final String getLibrary() {
    return driver.getLibrary();
  }
  
  @Override
  public String toString() {
    return super.toString() + ":  library=" + getLibrary();
  }
  
  private void setup() throws DriverException {

    final PKCS11 pk = getPK();
    
    CK_SLOT_INFO slotInfo;
    try {
      slotInfo = pk.C_GetSlotInfo(getNumber());
    }catch(PKCS11Exception e) {
      throw new DriverFailException("Unabled to get slot information from number " + getNumber() + " driver: " + driver, e);
    }

    this.description = Objects.toString(slotInfo.slotDescription, UNKNOWN_INFORMATION).trim();
    this.manufacturerId = Objects.toString(slotInfo.manufacturerID, UNKNOWN_INFORMATION).trim();
    this.firmwareVersion = Objects.toString(slotInfo.firmwareVersion, UNKNOWN_INFORMATION).trim();
    this.hardwareVersion = Objects.toString(slotInfo.hardwareVersion, UNKNOWN_INFORMATION).trim();
    
    CK_TOKEN_INFO tokenInfo;
    try {
      tokenInfo = pk.C_GetTokenInfo(getNumber());
    } catch (PKCS11Exception e) {
      throw new DriverFailException("Unabled to get token information on " + this, e);
    }

    this.token = new PKCS11Token.Builder(this)
      .withMinPinLen(tokenInfo.ulMinPinLen)
      .withMaxPinLen(tokenInfo.ulMaxPinLen)
      .withLabel(new String(tokenInfo.label))
      .withModel(new String(tokenInfo.model))
      .withSerial(new String(tokenInfo.serialNumber))
      .withManufacture(new String(tokenInfo.manufacturerID))
      .build();
    
    this.device = new DefaultDevice.Builder(IDevice.Type.A3)
      .withDriver(this.getLibrary())
      .withSlot(this)
      .withLabel(this.token.getLabel())
      .withModel(this.token.getModel())
      .withSerial(this.token.getSerial())
      .withCertificates(this.token.getCertificates())
      .build();
  }
}
