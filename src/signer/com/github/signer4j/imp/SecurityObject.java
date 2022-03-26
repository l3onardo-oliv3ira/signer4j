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

import static com.github.signer4j.imp.Signer4JInvoker.SIGNER4J;

import com.github.signer4j.ICertificateChooser;
import com.github.signer4j.IChoice;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.function.Supplier;

public abstract class SecurityObject {
  
  private final ICertificateChooser chooser;
  private final Runnable dispose;

  public SecurityObject(ICertificateChooser chooser, Runnable dispose) {
    this.chooser = Args.requireNonNull(chooser, "chooser is null");
    this.dispose = Args.requireNonNull(dispose, "dispose is null");
  }
  
  protected final <T> T invoke(Supplier<T> tryBlock) throws Signer4JException {
    return SIGNER4J.invoke(tryBlock, (e) -> dispose.run());
  }
  
  protected final IChoice choose() throws Signer4JException {
    IChoice choice = chooser.choose();
    if (choice.isCanceled())
      throw new CanceledOperationException();
    return choice;
  }
}
