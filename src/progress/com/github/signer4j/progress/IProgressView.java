package com.github.signer4j.progress;

import com.github.signer4j.progress.imp.IAttachable;

public interface IProgressView extends IProgress, IAttachable {

  void display();

  void undisplay(); 
  
  IProgressView reset();
}
