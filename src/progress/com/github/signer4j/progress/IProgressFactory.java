package com.github.signer4j.progress;

import java.util.function.Supplier;

public interface IProgressFactory extends Supplier<IProgressView> {
  
  String PARAM_NAME = IProgressFactory.class.getSimpleName();

}
