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


package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.util.Arrays;

public class CMSProcessableByteRangeArray implements CMSTypedData, CMSReadable {
  
  private final ASN1ObjectIdentifier type;
  private final byte[]  bytes;
  private final int offset;
  private final int length;
  
  public CMSProcessableByteRangeArray(byte[] bytes) {
    this(CMSObjectIdentifiers.data, bytes, 0, bytes.length);
  }

  public CMSProcessableByteRangeArray(byte[] bytes, int offset, int length) {
    this(CMSObjectIdentifiers.data, bytes, offset, length);
  }

  public CMSProcessableByteRangeArray(ASN1ObjectIdentifier type, byte[]  bytes, int offset, int length) {
    this.type = type;
    this.bytes = bytes;
    this.offset = offset;
    this.length = length;
  }

  @Override
  public InputStream getInputStream() {
    return new ByteArrayInputStream(bytes, offset, length);
  }

  @Override
  public void write(OutputStream zOut) throws IOException, CMSException {
    zOut.write(bytes, offset, length);
  }

  @Override
  public Object getContent() {
    return Arrays.copyOfRange(bytes,  offset,  length);
  }

  @Override
  public ASN1ObjectIdentifier getContentType() {
    return type;
  }
}
