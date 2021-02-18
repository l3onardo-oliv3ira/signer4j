package com.github.signer4j.imp;

import java.io.File;

public class SmartLookupStrategy extends StrategyWrapper {

  public SmartLookupStrategy() {
    this(SystemSupport.getDefault());
  }
  
  public SmartLookupStrategy(SystemSupport support) {
    super(LookupStrategy.notDuplicated(support)
      .more(new EnvironmentStrategy(support))
    );
  }

  public SmartLookupStrategy(SystemSupport support, File config) {
    super(LookupStrategy.notDuplicated(support)
     .more(new EnvironmentStrategy(support))
     .more(new FileStrategy(config))
   );
  }
}

