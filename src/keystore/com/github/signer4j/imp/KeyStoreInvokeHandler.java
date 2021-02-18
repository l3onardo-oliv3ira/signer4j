package com.github.signer4j.imp;

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
import com.github.signer4j.imp.exception.KeyStoreAccessException;
import com.github.signer4j.imp.exception.LoginCanceledException;
import com.github.signer4j.imp.exception.ModuleException;
import com.github.signer4j.imp.exception.NoTokenPresentException;
import com.github.signer4j.imp.exception.OutOfMemoryException;
import com.github.signer4j.imp.exception.PrivateKeyNotFound;
import com.github.signer4j.imp.exception.TokenLockedException;

public class KeyStoreInvokeHandler extends InvokeHandler<KeyStoreAccessException> {
  
  public static final KeyStoreInvokeHandler INVOKER = new KeyStoreInvokeHandler();
  
  private KeyStoreInvokeHandler() {}
  
  @Override
  public <T> T invoke(Supplier<T> tryBlock, Consumer<Throwable> catchBlock, Runnable finallyBlock) throws KeyStoreAccessException {
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
      final String message = Strings.trim(e.getMessage());
      if ("No token present".equalsIgnoreCase(message) || "Token is not present".equalsIgnoreCase(message)) 
        throw new NoTokenPresentException(e);
      if ("Unable to perform password callback".equalsIgnoreCase(message) || 
          Throwables.hasCause(e, CanceledOperationException.class))
        throw new LoginCanceledException(e);
      Throwable cause = e.getCause();
      if (cause != null && "CKR_PIN_LOCKED".equals(cause.getMessage()))
        throw new TokenLockedException(cause);
      throw new InvalidPinException(e);
    } catch(KeyStoreAccessException e) {
      catchBlock.accept(e);
      throw e;
    } catch (Exception e) {
      catchBlock.accept(e);
      String message = e instanceof NullPointerException ? "Token has been removed" : e.getMessage();
      /**
       * O provider SunPKCS11 do JAVA tem um bug que lança NullPointerException e corrompe a instância 
       * quando se remove o token após exibir a tela para digitação da senha (antes de informá-la).
       * Embora uma ação atípica e não recomendada (remover o token enquanto estiver em uso), o mala
       * do usuário sempre poderá fazer isso, portanto, infelizmente um NullPointerException 
       * esta sendo interpretado como um NoTokenPresent. O provider será reciclado 
       * automaticamente por DISPOSE_ACTION e um novo login providenciará uma nova
       * instância não corrompida de SunPKCS11, mantendo a estabilidade do sistema.
       * */
      if ("Token has been removed".equalsIgnoreCase(message))
        throw new NoTokenPresentException(e);
      if ("keystore password was incorrect".equalsIgnoreCase(message) || 
          Throwables.hasCause(e, UnrecoverableKeyException.class) ||
          Throwables.hasCause(e, BadPaddingException.class) ||
          Throwables.hasCause(e, FailedLoginException.class))
        throw new InvalidPinException(e); 
      throw new ModuleException(e);
    } catch (OutOfMemoryError e) {
      catchBlock.accept(e);
      throw new OutOfMemoryException(e);
    } finally {
      finallyBlock.run();
    }
  }
}
