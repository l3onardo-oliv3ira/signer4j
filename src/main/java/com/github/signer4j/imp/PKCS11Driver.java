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

import java.beans.Transient;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.github.signer4j.ILibraryAware;
import com.github.signer4j.ISlot;
import com.github.signer4j.exception.DriverException;
import com.github.signer4j.exception.DriverFailException;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Streams;

import sun.security.pkcs11.wrapper.CK_C_INITIALIZE_ARGS;
import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.pkcs11.wrapper.PKCS11Exception;

@SuppressWarnings("restriction")
class PKCS11Driver extends AbstractDriver implements ILibraryAware {

  private PKCS11 pk;
  
  private final String library;
  
  PKCS11Driver(Path library) {
    this.library = Args.requireNonNull(library, "null library")
      .toFile()
      .getAbsolutePath()
      .replace('\\', '/');
  }

  @Transient
  final PKCS11 getPK() {
    return pk;
  }
  
  @Override
  public final String getId() {
    return getLibrary();
  }
  
  @Override
  public final String getLibrary() {
    return library;
  }
  
  @Override
  protected final boolean isSame(AbstractDriver obj) {
    return Streams.isSame(library, ((PKCS11Driver)obj).library);
  }

  @Override
  protected void loadSlots(List<ISlot> output) throws DriverException {
    final CK_C_INITIALIZE_ARGS initArgs = new CK_C_INITIALIZE_ARGS();

    try {
        this.pk = PKCS11.getInstance(getLibrary(), "C_GetFunctionList", initArgs, false);
    } catch (IOException | PKCS11Exception e) {
      throw new DriverFailException("Unabled to create PKCS11 instance", e);
    }
    
    long[] slots = new long[0];
    
    try {
      slots = pk.C_GetSlotList(true);
    } catch (PKCS11Exception e) {
      throw new DriverFailException("Unabled to list slot from driver: " + this, e);
    }
    
    for(final long slot: slots) {
      try {
        PKCS11Slot s = new PKCS11Slot(this, slot);
        output.add(s);
        addDevice(s.toDevice());
      }catch(DriverException e) {
        handleException(e);
      }
    }
  }
}
