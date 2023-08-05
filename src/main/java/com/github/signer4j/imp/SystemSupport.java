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

import com.github.signer4j.IPathCollectionStrategy;
import com.github.signer4j.IWindowLockDettector;
import com.github.utils4j.imp.Jvms;

public enum SystemSupport implements IModuleExtension {
  WINDOWS(".dll") {
    @Override
    protected IPathCollectionStrategy createDriverLookupStrategy() {
      return new ForWindowsStrategy();
    }

    @Override
    protected IWindowLockDettector getWindowLockDettector() {
      return WindowLockDettector.WINDOWS;
    }
  },
  
  LINUX(".so") {
    @Override
    protected IPathCollectionStrategy createDriverLookupStrategy() {
      return new ForLinuxStrategy();
    }

    @Override
    protected IWindowLockDettector getWindowLockDettector() {
      return WindowLockDettector.LINUX;
    }
  },
  
  MAC(".dylib") {
    @Override
    protected IPathCollectionStrategy createDriverLookupStrategy() {
      return new ForMacStrategy();
    }
    
    @Override
    protected IWindowLockDettector getWindowLockDettector() {
      return WindowLockDettector.MAC;
    }

  },
  
  UNKNOWN(".unknown") {
    @Override
    protected IPathCollectionStrategy createDriverLookupStrategy() {
      return IPathCollectionStrategy.NOTHING;
    }
    
    @Override
    protected IWindowLockDettector getWindowLockDettector() {
      return WindowLockDettector.IDLE;
    }
  };

  private static SystemSupport SYSTEM;
  
  public static SystemSupport getDefault() {
    return SYSTEM != null ? SYSTEM : ( SYSTEM = (
        Jvms.isWindows() ? WINDOWS : (
        Jvms.isUnix() ? LINUX: (
        Jvms.isMac() ? MAC : UNKNOWN
    ))));
  }

  private final String extension;

  private IPathCollectionStrategy finder;
  
  SystemSupport(String extension) {
    this.extension = extension;
  }
  
  @Override
  public String getExtension() {
    return extension;
  }

  public final IPathCollectionStrategy getStrategy() {
    return finder == null ? finder = createDriverLookupStrategy() : finder;
  }

  protected abstract IPathCollectionStrategy createDriverLookupStrategy();

  protected abstract IWindowLockDettector getWindowLockDettector();
}
