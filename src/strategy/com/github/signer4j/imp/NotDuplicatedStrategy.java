package com.github.signer4j.imp;

import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.github.signer4j.IDriverLookupStrategy;
import com.github.signer4j.IDriverSetup;
import com.github.signer4j.IDriverVisitor;

public class NotDuplicatedStrategy implements IDriverLookupStrategy {

  private final Set<IDriverSetup> setups = new HashSet<>();
  
  private final List<IDriverLookupStrategy> strategies;
  
  public NotDuplicatedStrategy(IDriverLookupStrategy ... strategies) {
    Args.requireNonNull(strategies, "Unabled to create compounded strategy with null params");
    this.strategies = Stream.of(strategies).filter(f -> f != null).collect(toList());
  } 
  
  @Override
  public void lookup(IDriverVisitor visitor) {
    setups.clear();
    strategies.stream().forEach(s -> s.lookup(setups::add));
    setups.stream().forEach(visitor::visit);
  }

  public NotDuplicatedStrategy more(IDriverLookupStrategy strategy) {
    if (strategies != null) {
      strategies.add(strategy);
    }
    return this;
  }
}
