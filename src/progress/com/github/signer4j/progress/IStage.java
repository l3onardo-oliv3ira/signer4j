package com.github.signer4j.progress;

public interface IStage {

  public default String beginString() {
    return toString();
  }

  public default String endString() {
    return "Fim";
  }

  public default String interval() {
    return "";
  }
}
