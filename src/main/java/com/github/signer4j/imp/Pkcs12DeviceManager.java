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
import java.util.Set;

import com.github.signer4j.IDeviceAccessor;
import com.github.signer4j.IDeviceManager;
import com.github.utils4j.imp.Args;

public class Pkcs12DeviceManager extends AbstractDeviceAccessor implements IDeviceManager {

  private static final PKCS12Driver PKCS12_DRIVER = PKCS12Driver.getInstance();
  
  public Pkcs12DeviceManager() {}
  
  @Override
  public final Repository getRepository() {
    return Repository.NATIVE;
  }

  @Override
  public final IDeviceAccessor uninstall(Path ...pkcs12Files) {
    Args.requireNonNull(pkcs12Files, "Unabled to uninstall driver with null path's");
    PKCS12_DRIVER.uninstall(pkcs12Files);
    return this;
  }
  
  @Override
  public final IDeviceAccessor uninstallPkcs12() {
    PKCS12_DRIVER.uninstall();
    return this;
  }
  
  @Override
  public final IDeviceManager install(Path ... pkcs12File) {
    Args.requireNonNull(pkcs12File, "Unabled to install driver with null path's");
    uninstallPkcs12();
    PKCS12_DRIVER.install(pkcs12File);
    return this;
  }

  @Override
  protected final void load(Set<IDriver> drivers) {
    drivers.add(PKCS12_DRIVER);
  }
}
