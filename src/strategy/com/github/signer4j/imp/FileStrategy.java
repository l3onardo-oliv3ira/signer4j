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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.IDriverLookupStrategy;
import com.github.signer4j.IDriverVisitor;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Strings;

class FileStrategy extends AbstractStrategy implements IDriverLookupStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileStrategy.class);
  
  private final File file;

  public FileStrategy(File file) {
    this.file = Args.requireNonNull(file, "file can't be null");
  }

  @Override
  public void lookup(IDriverVisitor visitor) {
    try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while((line = reader.readLine()) != null) {
        line = Strings.trim(line);
        if (line.isEmpty())
          continue;
        createAndVisit(Paths.get(line), visitor);
      }
    } catch (IOException e) {
      LOGGER.debug("Exceção durante a leitura de lib's em " + file.getAbsolutePath(), e);
    }
  }
}
