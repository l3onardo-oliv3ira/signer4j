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

import static com.github.signer4j.TokenType.A1;
import static com.github.utils4j.gui.imp.SwingTools.isTrue;

import java.util.Optional;

import com.github.signer4j.IAuthStrategy;
import com.github.signer4j.IToken;
import com.github.signer4j.gui.alert.TokenUseAlert;
import com.github.signer4j.imp.exception.InvalidPinException;
import com.github.signer4j.imp.exception.LoginCanceledException;
import com.github.signer4j.imp.exception.NoTokenPresentException;
import com.github.signer4j.imp.exception.Signer4JException;

public enum AuthStrategy implements IAuthStrategy {
  
  ONE_TIME("Solicitar senha uma vez") {
    
    @Override
    protected void doLogin(IToken token) throws Signer4JException {
      
      if (!A1.equals(token.getType())) {
        super.doLogin(token);
        return;
      }

      Safe.BOX.authenticate(token.getSerial(), token::login);
    }
  },
  
  AWAYS("Sempre solicitar senha") {

    @Override
    protected final void doLogout(IToken token) {
      token.logout();
    }
  },  
  
  CONFIRM("Apenas confirmar uso do dispositivo") {

    @Override
    protected final void preLogin(IToken token, boolean isUsing) throws Signer4JException {
      if (!isUsing && !isTrue(TokenUseAlert::display)) {
        Signer4jContext.discardQuietly();
        token.logout();
        throw new LoginCanceledException();
      }
    }
    
    @Override
    protected void doLogin(IToken token) throws Signer4JException {
      ONE_TIME.doLogin(token);
    }
  };

  public static Optional<AuthStrategy> forName(String name) {
    try {
      return Optional.of(AuthStrategy.valueOf(name));
    } catch (Exception e) {
      return Optional.empty();
    }
  }
  
  private final String label;  
  
  AuthStrategy(String message) {
    this.label = message;
  }
  
  public final String getLabel() {
    return label;
  }
  
  public final void logout(IToken token) {
    doLogout(token);
    Safe.BOX.close();
  }
  
  protected void doLogout(IToken token) {}

  protected void doLogin(IToken token) throws Signer4JException {
    token.login();
  }

  protected void preLogin(IToken token, boolean isUsing) throws Signer4JException {
    ;//default nothing to do
  }

  @Override
  public final void login(IToken token, boolean isUsing) throws Signer4JException {
    
    if (Signer4jContext.isDiscarded())
      throw new LoginCanceledException();
    
    preLogin(token, isUsing);
    
    if (!token.isAuthenticated()) { 
      try {
        doLogin(token);
        Safe.BOX.open();
      } catch(NoTokenPresentException | InvalidPinException e) {
        throw e;
      
      } catch(Signer4JException e) {
        Signer4jContext.discardQuietly();
        throw e;
      }
    }
  }
}
