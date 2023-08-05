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

import java.util.Optional;

import com.github.signer4j.ICertificateChooser;
import com.github.signer4j.ISignatureAlgorithm;
import com.github.signer4j.ISignedData;
import com.github.signer4j.ISignerBuilder;
import com.github.signer4j.ISimpleSigner;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Args;

class SimpleSigner extends SecurityObject implements ISimpleSigner {

  private Optional<Object> lock;
  
  private ISignatureAlgorithm algorithm;
  
  private SimpleSigner(ICertificateChooser chooser, Runnable dispose) {
    super(chooser, dispose);
  }

  @Override
  public final ISignedData process(byte[] content, int offset, int length) throws Signer4JException {
    Args.requireNonEmpty(content, "content is null");
    Args.requireZeroPositive(offset, "offset is negative");
    Args.requirePositive(length, "length is not positive");
    return invoke(() -> (
      lock.isPresent() ? 
        choose(lock.get()) : 
        choose()
      ).toSigner(algorithm).sign(content, offset, length)
    );
  }
  
  public static class Builder implements ISignerBuilder {

    private final Runnable dispose;

    private final ICertificateChooser chooser;
    
    private ISignatureAlgorithm algorithm = SignatureAlgorithm.SHA1withRSA;

    private Optional<Object> lock = Optional.empty();
    
    public Builder(ICertificateChooser chooser, Runnable dispose) {
      this.chooser = Args.requireNonNull(chooser, "chooser is null");
      this.dispose = Args.requireNonNull(dispose, "dispose is null");
    }

    @Override
    public final ISignerBuilder usingAlgorithm(ISignatureAlgorithm algorithm) {
      this.algorithm = Args.requireNonNull(algorithm, "Unabled to using null algorigthm");
      return this;
    }
    
    @Override
    public final ISignerBuilder usingLock(Object lock) {
      this.lock = Optional.ofNullable(lock);
      return this;
    }
    
    @Override
    public final ISimpleSigner build() {
      SimpleSigner signer = new SimpleSigner(chooser, dispose);
      signer.lock = lock;
      signer.algorithm = algorithm;
      return signer;
    }
  }
}




  
