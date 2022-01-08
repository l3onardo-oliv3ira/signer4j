package com.github.signer4j.progress.imp;

import com.github.signer4j.progress.IProgressFactory;
import com.github.signer4j.progress.IProgressView;

public enum ProgressFactory implements IProgressFactory {
  DEFAULT;

  @Override
  public IProgressView get() {
    return new StepProgress();
  }
}
