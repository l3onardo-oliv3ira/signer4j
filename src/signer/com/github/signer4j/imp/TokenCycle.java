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

import java.util.concurrent.atomic.AtomicInteger;

import com.github.signer4j.IAuthStrategy;
import com.github.signer4j.IToken;
import com.github.signer4j.ITokenCycle;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Threads;

import io.reactivex.disposables.Disposable;

public abstract class TokenCycle extends TokenWrapper implements ITokenCycle {
  
  private static long LOGOUT_BATCH_TIMEOUT = 2000;
  
  private final Object lock;

  private final Disposable ticket;
  
  private final IAuthStrategy strategy;
  
  private final AtomicInteger refCount = new AtomicInteger(0);
  
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
  
  private static boolean hasTimeout(long startTime) {
    return System.currentTimeMillis() - startTime > LOGOUT_BATCH_TIMEOUT;
  }
  
  private boolean hasUse() {
    return refCount.get() > 0;
  }

  @Override
  public IToken login() throws Signer4JException {
    synchronized(lock) {
      strategy.login(super.token, hasUse());
      refCount.incrementAndGet();
    }
    return this;
  }

  @Override
  public final void logout() {
    if (hasUse() && refCount.decrementAndGet() == 0) {
      logoutAsync();
    }
  }

  private void logoutAsync() {
    long logoutTime = System.currentTimeMillis();
    /**
     * Um logout efetivamente assíncrono permite que os ciclos de autenticação funcionem 
     * de forma transparente tanto com lotes originados de múltiplas requisições simultâneas
     * quanto por lote em requisição única
     */
    startDaemon(() -> { 
      do {
        synchronized(lock) {
          if (hasUse())
            return;
          if (hasTimeout(logoutTime)) {
            strategy.logout(super.token);
            return;
          }
        }
        Threads.sleep(LOGOUT_BATCH_TIMEOUT);
      }while(true);
    }, LOGOUT_BATCH_TIMEOUT);
  }

  public final void dispose() {
    ticket.dispose();
  }
  
  @Override
  public final void logout(boolean force) {
    if (force) {
      synchronized(lock) {
        super.logout();
        refCount.set(0);
      }
      return;
    }
    logout();
  }
}
