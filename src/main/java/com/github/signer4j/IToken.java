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

import com.github.signer4j.gui.PasswordDialogCallbackHandler;
import com.github.signer4j.imp.LiteralPasswordCallbackHandler;
import com.github.signer4j.imp.exception.Signer4JException;

public interface IToken extends IGadget {
  
  String getManufacturer();
  
  long getMinPinLen();
  
  long getMaxPinLen();
  
  boolean isAuthenticated();
  
  TokenType getType();

  ISlot getSlot();
  
  IKeyStoreAccess getKeyStoreAccess();
  
  ICertificates getCertificates();

  ISignerBuilder signerBuilder();
  
  ISignerBuilder signerBuilder(ICertificateChooserFactory factory);
  
  ICMSSignerBuilder cmsSignerBuilder();
  
  ICMSSignerBuilder cmsSignerBuilder(ICertificateChooserFactory factory);

  IPKCS7SignerBuilder pkcs7SignerBuilder();
  
  IPKCS7SignerBuilder pkcs7SignerBuilder(ICertificateChooserFactory factory);
  
  ICertificateChooser createChooser(ICertificateChooserFactory factory);

  default ICertificateChooser createChooser() {
    return createChooser(ICertificateChooserFactory.DEFAULT);
  }

  void logout();
  
  IToken login(IPasswordCallbackHandler callback) throws Signer4JException;

  default IToken login() throws Signer4JException { 
    return login(IPasswordCollector.NOTHING);
  }
  
  default IToken login(IPasswordCollector collector) throws Signer4JException {
    return login(new PasswordDialogCallbackHandler(this, collector));
  }
  
  default IToken login(char[] password) throws Signer4JException {
    return login(new LiteralPasswordCallbackHandler(password));
  }
}
