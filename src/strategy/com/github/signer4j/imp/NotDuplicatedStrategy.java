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

import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.github.signer4j.IDriverLookupStrategy;
import com.github.signer4j.IDriverSetup;
import com.github.signer4j.IDriverVisitor;
import com.github.utils4j.imp.Args;

public class NotDuplicatedStrategy implements IDriverLookupStrategy {

  private final Set<IDriverSetup> setups = new HashSet<>();
  
  private final List<IDriverLookupStrategy> strategies;
  
  public NotDuplicatedStrategy(IDriverLookupStrategy ... strategies) {
    Args.requireNonNull(strategies, "Unabled to create compounded strategy with null params");
    this.strategies = Stream.of(strategies).filter(f -> f != null).collect(toList());
  } 
  
  @Override
  public void lookup(IDriverVisitor visitor) {
    setups.clear();
    strategies.stream().forEach(s -> s.lookup(setups::add));
    setups.stream().forEach(visitor::visit);
  }

  public NotDuplicatedStrategy more(IDriverLookupStrategy strategy) {
    if (strategies != null) {
      strategies.add(strategy);
    }
    return this;
  }
}
