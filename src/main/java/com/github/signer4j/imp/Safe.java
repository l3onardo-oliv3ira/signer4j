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

import java.util.HashMap;
import java.util.Map;

import com.github.signer4j.IToken;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.function.ICreator;

enum Safe {
  BOX;
  
  interface IAuthenticator extends ICreator<char[], IToken, Signer4JException> {}
  
  private boolean enabled = true;
  
  private final Map<String, char[]> vault = new HashMap<>();

  final void clear() {
    vault.clear();
  }

  final char[] read(String serial) {
    return enabled ? vault.get(serial) : null;
  }
  
  final char[] get(String serial) {
    return vault.get(serial);
  }

  final void put(String serial, char[] password) {
    vault.put(serial, password);
  }

  final void remove(String serial) {
    vault.remove(serial);
  }

  final void open() {
    this.enabled = true;
  }
  
  final void close() {
    this.enabled = false;
  }
  
  final void authenticate(String serial, IAuthenticator authenticator) throws Signer4JException {
    Args.requireNonNull(serial, "serial is null");
    Args.requireNonNull(authenticator, "function is null");
    authenticator.create(read(serial));
  }
}
