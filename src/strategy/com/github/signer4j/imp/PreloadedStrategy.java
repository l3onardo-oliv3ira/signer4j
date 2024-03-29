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

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.signer4j.IDriverVisitor;
import com.github.signer4j.IPathCollectionStrategy;

abstract class PreloadedStrategy implements IPathCollectionStrategy {

  private final List<String> searchedPaths = new ArrayList<>(60);
  
  private final Set<DriverSetup> libraries = new HashSet<DriverSetup>();
  
  protected final boolean load(String library) {
    searchedPaths.add(library);
    Optional<DriverSetup> ds = DriverSetup.create(Paths.get(library));
    if (ds.isPresent()) {
      return libraries.add(ds.get());
    }
    return false;
  }
  
  @Override
  public final List<String> queriedPaths() {
    return Collections.unmodifiableList(searchedPaths);
  }
  
  @Override
  public final void lookup(IDriverVisitor visitor) {
    libraries.forEach(visitor::visit);
  }
}
