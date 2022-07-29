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


package com.github.signer4j.cert.oid;

import static java.lang.Math.min;
import static java.util.Optional.ofNullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.utils4j.imp.Args;

class OIDBasic {

  private final String id;
  private final String content;
  
  private final Map<IMetadata, String> properties = new HashMap<IMetadata, String>();
  
  protected OIDBasic(String id, String content) {
    this.id = Args.requireText(id, "Unabled to create OID with empty id");
    this.content = Args.requireNonNull(content, "Unabled to create OID with null data");
  }

  public final String getOid() {
    return id;
  }
  
  protected final Optional<String> get(IMetadata field) {
    return ofNullable(properties.get(field));
  }

  protected final String getContent() {
    return content;
  }

  protected void setup() {
  }
  
  protected static interface IMetadata {
    int length();
  }
  
  protected void setup(IMetadata[] fields) {
    Args.requireNonNull(fields, "fields is null");
    int it = 0;
    for(IMetadata f: fields) {
      int length = f.length();
      properties.put(f, getNullIfDirty(content.substring(it, min(it + length, content.length()))));
      it += length;
    }
  }

  protected static String getNullIfDirty(String value) {
    if (value == null)
      return null;
    int length = (value = value.trim()).length();
    int i = 0;
    while (i < length && value.charAt(i) == '0') 
      i++;
    if (i == length)
      return null;
    return value;
  }
}
