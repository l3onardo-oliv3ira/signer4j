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

import static com.github.signer4j.IFilePath.toPaths;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.github.signer4j.IDriverVisitor;
import com.github.signer4j.IFilePath;
import com.github.signer4j.IToken;
import com.github.utils4j.imp.function.IBiProcedure;

public abstract class PKCS11TokenAccessor<T extends IToken> extends TokenAccessor<T> {
  
  private class FilePathStrategy extends AbstractStrategy {
    @Override
    public void lookup(IDriverVisitor visitor) {
      a3Libraries.forEach(fp -> createAndVisit(Paths.get(fp.getPath()), visitor));
    }
  }

  private volatile List<IFilePath> a1Files = new ArrayList<>();
  
  private volatile List<IFilePath> a3Libraries = new ArrayList<>();
  
  protected static interface IFileLoader extends IBiProcedure<List<IFilePath>, List<IFilePath>> {};
  
  protected PKCS11TokenAccessor(AuthStrategy strategy, IFileLoader loader) {
    super(strategy, new Signer4JDeviceManager(new PKCS11DeviceAccessor(), new Pkcs12DeviceManager()));
    loader.call(a1Files, a3Libraries);
    manager.setStrategy(new NotDuplicatedStrategy(new FilePathStrategy()));
    manager.install(toPaths(a1Files));
  }

  @Override
  protected void doCertificateAvailable(List<IFilePath> a1List, List<IFilePath> a3List) {
    //Heads up with deadlock! This method will run on the event dispatcher thread
    this.a3Libraries = a3List;
    this.a1Files = a1List;
  }
}
