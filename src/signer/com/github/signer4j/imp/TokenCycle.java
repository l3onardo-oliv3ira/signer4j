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

import static com.github.utils4j.imp.Threads.startDaemon;

import com.github.signer4j.IAuthStrategy;
import com.github.signer4j.IToken;
import com.github.signer4j.ITokenCycle;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Threads;

import io.reactivex.disposables.Disposable;

public abstract class TokenCycle extends TokenWrapper implements ITokenCycle {
  
  private static long LOGOUT_BATCH_TIMEOUT = 2000;
  
  private static boolean hasTimeout(long startTime) {
    return System.currentTimeMillis() - startTime > LOGOUT_BATCH_TIMEOUT;
  }

  private final Object lock;

  private final Disposable ticket;
  
  private final IAuthStrategy strategy;
  
  private volatile long effectiveLogoutTime = -1;

  private volatile int refCount = 0;

  public TokenCycle(IToken token, IAuthStrategy strategy, Object lock) {
    super(token);
    this.strategy = Args.requireNonNull(strategy, "strategy is null");
    this.lock = Args.requireNonNull(lock, "lock is null");
    this.ticket = getStatus().subscribe(this::checkStatus);
  }

  private void checkStatus(Boolean online) {
    if (!online) {
      logout(true);
    }
  }
  
  private boolean hasUse() {
    return refCount > 0 || (effectiveLogoutTime > 0 && !hasTimeout(effectiveLogoutTime));
  }

  public final void dispose() {
    logout(true);
    ticket.dispose();
  }

  @Override
  public IToken login() throws Signer4JException {
    synchronized(lock) {
      strategy.login(super.token, hasUse());
      ++refCount;
      effectiveLogoutTime = -1;
    }
    return this;
  }

  @Override
  public final void logout() {
    synchronized(lock) {
      if (refCount > 0 && --refCount == 0) {
        logoutAsync();
      }
    }
  }

  @Override
  public final void logout(boolean force) {
    if (force) {
      synchronized(lock) {
        super.logout();
        refCount = 0;
        effectiveLogoutTime = -1;
      }
      return;
    }
    logout();
  }
  
  private void logoutAsync() {

    long logoutRequestTime = effectiveLogoutTime = System.currentTimeMillis();
    /**
     * Um logout efetivamente assíncrono permite que os ciclos de autenticação funcionem 
     * de forma transparente tanto com lotes originados de múltiplas requisições simultâneas
     * quanto por lote em requisição única
     */
    startDaemon(() -> { 
      do {
        synchronized(lock) {
          if (refCount > 0) {
            return; //abort logout!
          }
          if (hasTimeout(logoutRequestTime)) {
            effectiveLogoutTime = System.currentTimeMillis();
            strategy.logout(super.token);
            return;
          }
        }
        Threads.sleep(LOGOUT_BATCH_TIMEOUT);
      }while(true);
    }, LOGOUT_BATCH_TIMEOUT);
  }
}
