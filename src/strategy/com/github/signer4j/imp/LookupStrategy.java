package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;

public class LookupStrategy {
  private LookupStrategy() {}
  
  public static NotDuplicatedStrategy notDuplicated() {
    return notDuplicated(SystemSupport.getDefault());
  }
  
  public static NotDuplicatedStrategy notDuplicated(SystemSupport support) {
    return new NotDuplicatedStrategy(requireNonNull(support, "Unabled to use null SystemSupport").getStrategy());
  }
}
