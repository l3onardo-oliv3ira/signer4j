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

import static com.github.utils4j.imp.Strings.trim;
import static com.github.utils4j.imp.Throwables.hasCause;

import java.security.UnrecoverableKeyException;
import java.util.function.Consumer;

import javax.crypto.BadPaddingException;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import com.github.signer4j.imp.exception.ExpiredCredentialException;
import com.github.signer4j.imp.exception.InvalidPinException;
import com.github.signer4j.imp.exception.LoginCanceledException;
import com.github.signer4j.imp.exception.ModuleException;
import com.github.signer4j.imp.exception.NoTokenPresentException;
import com.github.signer4j.imp.exception.OutOfMemoryException;
import com.github.signer4j.imp.exception.PrivateKeyNotFound;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.signer4j.imp.exception.TokenLockedException;
import com.github.utils4j.imp.InvokeHandler;
import com.github.utils4j.imp.Throwables;
import com.github.utils4j.imp.function.Supplier;

public class Signer4JInvoker extends InvokeHandler<Signer4JException> {
  
  public static final Signer4JInvoker SIGNER4J = new Signer4JInvoker();
  
  private Signer4JInvoker() {}
  
  @Override
  public <T> T invoke(Supplier<T> tryBlock, Consumer<Throwable> catchBlock, Runnable finallyBlock) throws Signer4JException {
    try {
      return tryBlock.get();
    } catch (CanceledOperationException e) {
      catchBlock.accept(e);
      throw new LoginCanceledException(e);
    } catch (PrivateKeyNotFound e) {
      catchBlock.accept(e);
      throw new NoTokenPresentException(e);
    } catch (AccountLockedException e) {
      catchBlock.accept(e);
      throw new TokenLockedException(e);
    } catch (CredentialExpiredException | AccountExpiredException e) {
      catchBlock.accept(e);
      throw new ExpiredCredentialException(e);
    } catch (FailedLoginException e) {
      catchBlock.accept(e);
      throw new InvalidPinException(e);
    } catch (LoginException e) {
      catchBlock.accept(e);      

      if (isLoginCanceled(e))
        throw new LoginCanceledException(e);
      
      if (isTokenLocked(e))
        throw new TokenLockedException(e);
      
      if (isNoTokenPresent(e))
        throw new NoTokenPresentException(e);
      
      throw new InvalidPinException(e);
    } catch(Signer4JException e) {
      catchBlock.accept(e);
      throw e;
    } catch (Exception e) {
      catchBlock.accept(e);

      if (isLoginCanceled(e))
        throw new LoginCanceledException(e);

      if (isTokenLocked(e))
        throw new TokenLockedException(e);

      /**
       * O provider SunPKCS11 do JAVA tem um bug em alguns driver's rodando em MSCAPI que lança 
       * NullPointerException e corrompe a instância quando se remove o token após exibir a 
       * tela para digitação da senha (mas antes de informá-la).
       * Embora uma ação atípica e não recomendada (remover o token enquanto o driver aguarda a informação da senha),  
       * nada impede que o usuário faça isso, portanto, infelizmente um NullPointerException 
       * esta sendo interpretado como um NoTokenPresent. O provider será reciclado 
       * automaticamente por DISPOSE_ACTION e um novo login providenciará uma nova
       * instância não corrompida de SunPKCS11, mantendo a estabilidade da aplicação.
       * */
      
      boolean noTokenPresent = e instanceof NullPointerException || isNoTokenPresent(e);

      if (noTokenPresent)
        throw new NoTokenPresentException(e);


      if (isPasswordIncorrect(e))
        throw new InvalidPinException(e); 
      
      throw new ModuleException(e);
    } catch (OutOfMemoryError e) {
      catchBlock.accept(e);
      throw new OutOfMemoryException(e);
    } finally {
      finallyBlock.run();
    }
  }
  
  private boolean isPasswordIncorrect(Throwable e) {
    return Throwables.traceStream(e)
      .map(t -> trim(t.getMessage()).toLowerCase())
      .anyMatch(m -> 
        m.contains("keystore password was incorrect") || 
        m.contains("ckr_pin_len_range")
      ) ||  
      hasCause(e, FailedLoginException.class)          ||
      hasCause(e, UnrecoverableKeyException.class)     ||
      hasCause(e, BadPaddingException.class);
  }

  private static boolean isLoginCanceled(Throwable e) {
    return hasCause(e, CanceledOperationException.class) || Throwables.traceStream(e)
      .map(t -> trim(t.getMessage()).toLowerCase())
      .anyMatch(m -> 
        m.contains("unable to perform password callback") ||
        m.contains("a operação foi cancelada pelo usuário")
      );
  }

  private static boolean isNoTokenPresent(Throwable e) {
    return Throwables.traceStream(e)
      .map(t -> trim(t.getMessage()).toLowerCase())
      .anyMatch(m -> 
        m.contains("token has been removed") || 
        m.contains("no token present")       || 
        m.contains("token is not present")   || 
        m.contains("ckr_token_not_present")  ||
        //block específico (catch Exception)
        m.contains("exception obtaining signature") ||
        m.contains("acesso negado")
      );
  }  
  
  private static boolean isTokenLocked(Throwable e) {
    return Throwables.traceStream(e)
      .map(t -> trim(t.getMessage()).toLowerCase())
      .anyMatch(m ->
        m.contains("ckr_pin_locked") ||
        m.contains("número máximo de tentativas para digitar o pin")                
      );
  }
}
