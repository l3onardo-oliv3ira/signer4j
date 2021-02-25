package com.github.signer4j.imp;

import java.util.List;
import java.util.Set;

import com.github.signer4j.IDevice;
import com.github.signer4j.IKeyStore;
import com.github.signer4j.IPasswordCallbackHandler;
import com.github.signer4j.ISlot;
import com.github.signer4j.IToken;
import com.github.signer4j.TokenType;
import com.github.signer4j.cert.ICertificateFactory;
import com.github.signer4j.exception.DriverException;
import com.github.signer4j.exception.DriverFailException;
import com.github.signer4j.imp.exception.KeyStoreAccessException;

public class MSCAPIDevManager extends AbstractDeviceManager {

  @Override
  protected void load(Set<IDriver> drivers) {
    drivers.add(new MSCAPIDriver());
  }
  
  private static final class MSCAPIDriver extends AbstractDriver {

    @Override
    public String getId() {
      return "mscapi";
    }

    @Override
    protected void loadSlots(List<ISlot> output) throws DriverException {
      ISlot slot = new MSCAPISlot();
      output.add(slot);
      addDevice(slot.toDevice());
    }
  }
  
  private static final class MSCAPISlot extends VirtualSlot {

    private MSCAPIToken token;
    
    protected MSCAPISlot() throws DriverException {
      super(-1, "Mscapi");
      this.setup();
    }

    @Override
    public IToken getToken() {
      return token;
    }

    private void setup() throws DriverException {
      this.device = new DefaultDevice.Builder(IDevice.Type.VIRTUAL)
        .withDriver(this.getFirmewareVersion())
        .withSlot(this)
        .withLabel("mscapi")
        .withModel("mscapi")
        .withSerial("mscapi")
        .build();
      
      this.token = new MSCAPIToken.Builder(this, device)
        .withLabel(this.device.getLabel())
        .withModel(this.device.getModel())
        .withSerial(getHardwareVersion())
        .withManufacture(getManufacturer())
        .build();
    }
  }
  
  private static class MSCAPIToken extends AbstractToken<MSCAPISlot> {

    private static final int MIN_PASSWORD_LENGTH = 1;
    private static final int MAX_PASSWORD_LENGTH = 31;
    
    private DefaultDevice device;

    private MSCAPIKeyStoreLoader loader;
    
    protected MSCAPIToken(MSCAPISlot slot, DefaultDevice device) {
      super(slot, TokenType.VIRTUAL);
      this.loader = new MSCAPIKeyStoreLoader(this.device = device, getDispose());
    }

    @Override
    public long getMinPinLen() {
      return MIN_PASSWORD_LENGTH;
    }

    @Override
    public long getMaxPinLen() {
      return MAX_PASSWORD_LENGTH;
    }
    
    @Override
    protected IKeyStore getKeyStore(IPasswordCallbackHandler callback) throws KeyStoreAccessException {
      return loader.getKeyStore();
    }
    
    @Override
    protected IToken loadCertificates(ICertificateFactory factory) throws DriverException {
      try(IKeyStore keyStore = loader.getKeyStore()) {
        this.device.setCertificates(this.certificates = new MSCAPICertificates(this, keyStore, factory));
      } catch (Exception e) {
        throw new DriverFailException("Não foi possível carregar os certificados", e);
      }
      return this;
    }
    
    static class Builder extends AbstractToken.Builder<MSCAPISlot, MSCAPIToken> {
      
      private DefaultDevice device;
      
      Builder(MSCAPISlot slot, DefaultDevice device) {
        super(slot);
        this.device = Args.requireNonNull(device, "device is null");
      }
      
      @Override
      protected AbstractToken<MSCAPISlot> createToken(MSCAPISlot slot) throws DriverException {
        return new MSCAPIToken(slot, device);
      }  
    }
  }
  
  private static class MSCAPICertificates extends KeyStoreCertificates {
    public MSCAPICertificates(MSCAPIToken token, IKeyStore keyStore, ICertificateFactory factory) throws KeyStoreAccessException {
      super(token, keyStore, factory);
    }
  }
}
