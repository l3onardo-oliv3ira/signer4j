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

import com.github.utils4j.imp.Threads;

public class SwitchRepositoryException extends Exception {

  private static final long serialVersionUID = -384838276379546577L;

  private long timeout;

  private final boolean waiting;

  private final Repository repository;

  private volatile boolean available = false;

  public SwitchRepositoryException(boolean waiting, Repository repository) { 
    this(waiting, repository, Signer4jContext.timeout());
  }

  private SwitchRepositoryException(boolean waiting, Repository repository, long timeout) {
    this.waiting = waiting;
    this.repository = repository;
    this.timeout = timeout + 1000; //tolerância de até 1 segundo
  }

  public final void setAvailable() {
    available = true;
  }  

  public final Repository getRepository() {
    return repository;
  }

  private boolean isAvailable(long from) {
    return available || (System.currentTimeMillis() - from) >= timeout;
  }

  /**
   * A espera deve ocorrer apenas pela thread que lançou a exceção 
   */
  public final void waitFor() {
    if (waiting) {
      long now = System.currentTimeMillis();        
      while(!isAvailable(now)) {
        Threads.sleep(50);
      }
    }
  }
}
