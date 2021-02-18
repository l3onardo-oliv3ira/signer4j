package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NotDuplicatedStrategy implements IDriverLookupStrategy {

  private final Set<IDriverSetup> setups = new HashSet<>();
  
  private final List<IDriverLookupStrategy> strategies;
  
  public NotDuplicatedStrategy(IDriverLookupStrategy ... strategies) {
    this.strategies = new ArrayList<>(asList(requireNonNull(strategies, "Unabled to create compounded strategy with null params")));
  } 
  
  @Override
  public void lookup(IDriverVisitor visitor) {
    setups.clear();
    for(IDriverLookupStrategy s: strategies) {
      s.lookup(setup -> setups.add(setup));
    }
    setups.forEach(s -> visitor.visit(s));
  }

  public NotDuplicatedStrategy more(IDriverLookupStrategy strategy) {
    if (strategies != null) {
      strategies.add(strategy);
    }
    return this;
  }
}
