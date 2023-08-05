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

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import com.github.signer4j.ICertificate;
import com.github.signer4j.ICertificateListUI.ICertificateEntry;
import com.github.signer4j.IDevice;
import com.github.utils4j.imp.Args;

public class DeviceCertificateEntry extends DefaultCertificateEntry {
  
  private DeviceCertificateEntry(IDevice device, ICertificate certificate) {
    super(device, certificate);
  }

  public final IDevice getNative() {
    return super.device;
  }
  
  public static List<ICertificateEntry> from(List<IDevice> devices, Predicate<ICertificate> filter) {
    Args.requireNonNull(devices, "devices is null");
    Args.requireNonNull(filter, "filter is null");
    return devices.stream().map(d -> d.getCertificates().filter(filter).map(c -> new DeviceCertificateEntry(d, c)).collect(toList())).flatMap(Collection::stream).collect(toList());
  }  
}
