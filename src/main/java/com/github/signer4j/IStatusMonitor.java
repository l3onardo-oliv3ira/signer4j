package com.github.signer4j;

import io.reactivex.Observable;

public interface IStatusMonitor {
  Observable<Boolean> getStatus();
}