package com.github.signer4j.imp;

import com.github.signer4j.IDriverLookupStrategy;
import com.github.signer4j.IDriverVisitor;
import com.github.utils4j.imp.Args;

class StrategyWrapper implements IDriverLookupStrategy {

  private final IDriverLookupStrategy strategy;
  
  public StrategyWrapper(IDriverLookupStrategy strategy) {
    this.strategy = Args.requireNonNull(strategy, "strategy can't be null");
  }
  
  @Override
  public final void lookup(IDriverVisitor visitor) {
    strategy.lookup(visitor);
  }
}
