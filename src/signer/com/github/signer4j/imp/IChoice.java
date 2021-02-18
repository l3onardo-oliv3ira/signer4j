package com.github.signer4j.imp;

import com.github.signer4j.IPersonalData;

public interface IChoice extends IPersonalData {
  String getProvider();
  
  boolean isCanceled();
}
