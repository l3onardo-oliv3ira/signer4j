package com.github.signer4j.progress.imp;

import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.IProgressFactory;

public enum ProgressFactory implements IProgressFactory {
  DEFAULT;

  @Override
  public IProgress get() {
    return new DefaultProgress();
  }
}
