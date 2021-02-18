package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;

import com.github.signer4j.IDevice;
import com.github.signer4j.ILibraryAware;
import com.github.signer4j.IToken;
import com.github.signer4j.exception.DriverException;
import com.github.signer4j.exception.DriverFailException;

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
    this.driver = requireNonNull(driver, "Unabled to create slot with null driver instance");
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
  public final String getManufacturerId() {
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
      throw new DriverFailException("Unabled to get slot information on " + this, e);
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
      .withSerial(this.token.getSerialNumber())
      .withCertificates(this.token.getCertificates())
      .build();
  }
}
