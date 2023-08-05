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

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import com.github.signer4j.IDevice;
import com.github.signer4j.IDeviceAccessor;
import com.github.signer4j.IDeviceManager;
import com.github.signer4j.IPasswordCallbackHandler;
import com.github.signer4j.ISlot;
import com.github.signer4j.IToken;
import com.github.signer4j.TokenType;
import com.github.signer4j.cert.ICertificateFactory;
import com.github.signer4j.exception.DriverException;
import com.github.signer4j.exception.DriverFailException;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Args;

public class MSCAPIDeviceManager extends AbstractDeviceAccessor implements IDeviceManager {

  @Override
  protected void load(Set<IDriver> drivers) {
    drivers.add(new MSCAPIDriver());
  }
  
  @Override
  public final Repository getRepository() {
    return Repository.MSCAPI;
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
    public final long getMinPinLen() {
      return MIN_PASSWORD_LENGTH;
    }

    @Override
    public final long getMaxPinLen() {
      return MAX_PASSWORD_LENGTH;
    }
    
    @Override
    protected IKeyStore getKeyStore(IPasswordCallbackHandler callback) throws Signer4JException {
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
    public MSCAPICertificates(MSCAPIToken token, IKeyStore keyStore, ICertificateFactory factory) throws Signer4JException {
      super(token, keyStore, factory);
    }
  }

  @Override
  public IDeviceAccessor install(Path... pkcs12Files) {
    //we have to go back here to launch windows cert manager using powershell
    return this;
  }

  @Override
  public IDeviceAccessor uninstall(Path... pkcs12File) {
    //we have to go back here to launch windows cert manager using powershell
    return this;
  }

  @Override
  public IDeviceAccessor uninstallPkcs12() {
    //we have to go back here to launch windows cert manager using powershell
    return this;
  }
}
