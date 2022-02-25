package com.github.signer4j.imp;

import static com.github.utils4j.imp.Args.requireNonNull;

import com.github.signer4j.IDriverLookupStrategy;
import com.github.signer4j.IDriverVisitor;

class StrategyWrapper implements IDriverLookupStrategy {

  private final IDriverLookupStrategy strategy;
  
  public StrategyWrapper(IDriverLookupStrategy strategy) {
    this.strategy = requireNonNull(strategy, "strategy can't be null");
  }
  
  @Override
  public final void lookup(IDriverVisitor visitor) {
    strategy.lookup(visitor);
  }
}
