package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;
import static com.github.signer4j.imp.KeyStoreInvokeHandler.INVOKER;

import com.github.signer4j.ICertificateChooser;
import com.github.signer4j.imp.exception.KeyStoreAccessException;

public abstract class SecurityObject {
  
  private final ICertificateChooser chooser;
  private final Runnable dispose;

  public SecurityObject(ICertificateChooser chooser, Runnable dispose) {
    this.chooser = requireNonNull(chooser, "chooser is null");
    this.dispose = requireNonNull(dispose, "dispose is null");
  }
  
  protected final <T> T invoke(Supplier<T> tryBlock) throws KeyStoreAccessException {
    return INVOKER.invoke(tryBlock, (e) -> dispose.run());
  }
  
  protected final IChoice choose() throws KeyStoreAccessException {
    IChoice choice = chooser.choose();
    if (choice.isCanceled())
      throw new CanceledOperationException();
    return choice;
  }
}
