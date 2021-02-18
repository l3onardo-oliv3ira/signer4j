package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;

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
