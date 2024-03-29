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


package com.github.signer4j;

import java.util.Optional;
import java.util.function.BiFunction;

import com.github.signer4j.imp.ConsoleChooser;
import com.github.signer4j.imp.DefaultChooser;
import com.github.signer4j.imp.SingleCertificateChooserFactory;
import com.github.signer4j.imp.exception.CertificateAliasNotFoundException;
import com.github.utils4j.imp.Args;

public interface ICertificateChooserFactory extends BiFunction<IKeyStoreAccess, ICertificates, ICertificateChooser> {
  static ICertificateChooserFactory CONSOLE = (k, c) -> new ConsoleChooser(k, c);
  static ICertificateChooserFactory DEFAULT = (k, c) -> new DefaultChooser(k, c);
  
  static ICertificateChooserFactory fromCertificate(Optional<ICertificate> certificate) {
    return fromCertificate(certificate, DEFAULT);
  }
  
  static ICertificateChooserFactory fromCertificate(Optional<ICertificate> certificate, ICertificateChooserFactory defaultIfNot) {
    Args.requireNonNull(certificate, "certificate is null");  
    Args.requireNonNull(defaultIfNot, "factory is null");    
    return certificate.isPresent() ? SingleCertificateChooserFactory.get(certificate.get().getAlias().orElseThrow(CertificateAliasNotFoundException::new)) : defaultIfNot;
  }
}
