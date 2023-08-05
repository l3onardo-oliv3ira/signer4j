package com.github.signer4j;

import java.util.Collections;
import java.util.List;

public interface IPathCollectionStrategy extends IDriverLookupStrategy {
  
  static IPathCollectionStrategy NOTHING = new IPathCollectionStrategy() {
    public void lookup(IDriverVisitor visitor) {}
    
    public List<String> queriedPaths() { return Collections.emptyList(); }
  };
  
  public List<String> queriedPaths();
}
 
