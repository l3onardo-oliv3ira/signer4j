package com.github.signer4j.progress;

import com.github.signer4j.progress.imp.ICanceller;

public interface IProgressView extends IProgress, ICanceller {

  void display();

  void undisplay(); 
  
  IProgressView reset();
}
