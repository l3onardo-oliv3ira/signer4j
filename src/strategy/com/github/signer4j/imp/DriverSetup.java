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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import com.github.signer4j.IDriverSetup;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Streams;

class DriverSetup implements IDriverSetup {
  
  public static Optional<DriverSetup> create(Path library) {
    try {
      return Optional.of(new DriverSetup(library.toAbsolutePath()));
    }catch(IOException e) {
      return Optional.empty();
    }
  }
  
  private final String md5;

  private final Path library;
  
  private DriverSetup(Path library) throws IOException {
    this.library = Args.requireNonNull(library, "Unabled to create driversupport with null library");
    this.md5 = Streams.md5(library.toFile());
  }
  
  @Override
  public final Path getLibrary() {
    return library;
  }
  
  @Override
  public final String getMd5() {
    return md5;
  }

  @Override
  public final int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((md5 == null) ? 0 : md5.hashCode());
    return result;
  }

  @Override
  public final boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DriverSetup other = (DriverSetup) obj;
    if (md5 == null) {
      if (other.md5 != null)
        return false;
    } else if (!md5.equals(other.md5))
      return false;
    return true;
  }
}
