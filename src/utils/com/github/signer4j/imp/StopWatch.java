package com.github.signer4j.imp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StopWatch {

  private static final Logger LOGGER = LoggerFactory.getLogger(StopWatch.class);
  
  private long total;

  private long start;

  private Logger logger;

  public StopWatch(){
    this(LOGGER);
  }

  public StopWatch(Logger logger){
    this.logger = logger;
  }

  public final Logger getLogger() {
    return this.logger;
  }
  
  public void start() {
    start = System.currentTimeMillis();
  }

  public long stop() {
    long diff = System.currentTimeMillis() - start;
    total += diff;
    return diff;
  }

  public long stop(String message){
    long time = stop();
    if (logger.isDebugEnabled()){
      logger.debug(message + ": " + time + "ms");
    }
    return time;
  }

  public void reset() {
    total = 0;
  }

  public void reset(long total) {
    this.total = total;
  }

  public long getTime() {
    return total;
  }
}
