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

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.github.signer4j.IConfigPersister;
import com.github.signer4j.IFilePath;

public class Config{
  
  private static IConfigPersister config;
  
  private static Image icon ;
  
  protected static void setup(Image image, IConfigPersister conf) {
    Config.icon = image;
    Config.config = conf != null ? conf: config;
    setupA3();
  }

  protected static IConfigPersister config() {
    return config != null ? config : (config = new ConfigPersister(new SignerConfig()));
  }

  public static Image getIcon() {
    return icon;
  }
  
  public static Optional<String> defaultCertificate() {
    return config().defaultCertificate();
  }

  public static Optional<String> defaultDevice() {
    return config().defaultDevice();
  }

  public static Optional<String> defaultAlias() {
    return config().defaultAlias();
  }

  public static void saveA1Paths(IFilePath... path) {
    config().saveA1Paths(path);
  }

  public static void saveA3Paths(IFilePath... path) {
    config().saveA3Paths(path);
  }

  public static void loadA1Paths(Consumer<IFilePath> add) {
    config().loadA1Paths(add);
  }

  public static void loadA3Paths(Consumer<IFilePath> add) {
    config().loadA3Paths(add);
  }

  public static void save(String defaultAlias) {
    config().save(defaultAlias);
  }
  
  public static void reset() {
    config().reset();
  }

  private static void setupA3() {
    List<IFilePath> libs = new ArrayList<>();
    loadA3Paths(libs::add);
    if (!libs.isEmpty())
      return;
    new SmartLookupStrategy().lookup(dv -> libs.add(new FilePath(dv.getLibrary())));
    saveA3Paths(libs.toArray(new FilePath[libs.size()]));
  }
  
  protected Config() {}
}

