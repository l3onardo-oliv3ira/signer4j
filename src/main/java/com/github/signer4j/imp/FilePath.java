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
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.signer4j.IFilePath;
import com.github.utils4j.imp.Args;

public class FilePath implements IFilePath {
  
  private final Path path;
  
  public FilePath(Path path) {
    this.path = Args.requireNonNull(path, "path is null");
  }
  
  @Override
  public final String getPath() {
    return path.toFile().getAbsolutePath();
  }
  
  @Override
  public final Path toPath() {
    return path;
  }
  
  @Override
  public final String toString() {
    return getPath();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((path == null) ? 0 : path.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FilePath other = (FilePath) obj;
    if (path == null) {
      if (other.path != null)
        return false;
    } else {
      try {
        if (!Files.isSameFile(path, other.path))
          return false;
      } catch (IOException e) {
        return false;
      }
    }
    return true;
  }
}
