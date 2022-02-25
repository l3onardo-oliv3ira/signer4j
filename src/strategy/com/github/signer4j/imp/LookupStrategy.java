package com.github.signer4j.imp;

import com.github.utils4j.imp.Args;

public class LookupStrategy {
  private LookupStrategy() {}
  
  public static NotDuplicatedStrategy notDuplicated() {
    return notDuplicated(SystemSupport.getDefault());
  }
  
  public static NotDuplicatedStrategy notDuplicated(SystemSupport support) {
    Args.requireNonNull(support, "Unabled to use null SystemSupport");
    return new NotDuplicatedStrategy(support.getStrategy());
  }
}
