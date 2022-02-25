package com.github.signer4j.imp;

import static com.github.signer4j.imp.Signer4JInvoker.INVOKER;
import static com.github.utils4j.imp.Args.requireNonNull;

import com.github.signer4j.ICertificateChooser;
import com.github.signer4j.IChoice;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.function.Supplier;

public abstract class SecurityObject {
  
  private final ICertificateChooser chooser;
  private final Runnable dispose;

  public SecurityObject(ICertificateChooser chooser, Runnable dispose) {
    this.chooser = requireNonNull(chooser, "chooser is null");
    this.dispose = requireNonNull(dispose, "dispose is null");
  }
  
  protected final <T> T invoke(Supplier<T> tryBlock) throws Signer4JException {
    return INVOKER.invoke(tryBlock, (e) -> dispose.run());
  }
  
  protected final IChoice choose() throws Signer4JException {
    IChoice choice = chooser.choose();
    if (choice.isCanceled())
      throw new CanceledOperationException();
    return choice;
  }
}
