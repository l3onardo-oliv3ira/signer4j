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

import static com.github.utils4j.gui.imp.SwingTools.isTrue;

import com.github.signer4j.IAuthStrategy;
import com.github.signer4j.IToken;
import com.github.signer4j.gui.alert.TokenUseAlert;
import com.github.signer4j.imp.exception.InvalidPinException;
import com.github.signer4j.imp.exception.LoginCanceledException;
import com.github.signer4j.imp.exception.NoTokenPresentException;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.BooleanTimeout;

public enum AuthStrategy implements IAuthStrategy{
  
  ONE_TIME("Solicitar senha uma vez"),
  
  AWAYS("Sempre solicitar senha") {

    @Override
    public final void logout(IToken token) {
      token.logout();
    }
  },  
  
  CONFIRM("Apenas confirmar uso do dispositivo") {

    @Override
    protected final void preLogin(IToken token, boolean hasUse) throws LoginCanceledException {
      if (!hasUse && !isTrue(TokenUseAlert::display)) {
        discard.setTrue();
        token.logout();
        throw new LoginCanceledException();
      }
    }  
  };

  
  private final String label;
  
  protected final BooleanTimeout discard = new BooleanTimeout(2000);
  
  AuthStrategy(String message) {
    this.label = message;
  }
  
  public final String geLabel() {
    return label;
  }
  
  public void logout(IToken token) {
    ;//default nothing to do
  }
  
  protected void preLogin(IToken token, boolean hasUse) throws LoginCanceledException {
    ;//default nothing to do
  }

  @Override
  public void login(IToken token, boolean hasUse) throws Signer4JException {
    
    if (discard.isTrue())
      throw new LoginCanceledException();
    
    preLogin(token, hasUse);
    
    if (!token.isAuthenticated()) { 
      try {
        token.login();
      } catch(NoTokenPresentException | InvalidPinException e) {
        throw e;
      } catch(Signer4JException e) {
        discard.setTrue();
        throw e;
      }
    }
  }
}
