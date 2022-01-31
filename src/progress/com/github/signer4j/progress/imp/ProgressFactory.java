package com.github.signer4j.progress.imp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.signer4j.progress.IProgressFactory;
import com.github.signer4j.progress.IProgressView;

import io.reactivex.disposables.Disposable;

public enum ProgressFactory implements IProgressFactory {
  DEFAULT;

  private Map<String, Entry> steps = Collections.synchronizedMap(new HashMap<>());
  
  @Override
  public IProgressView get() {
    StepProgress sp =  new StepProgress();
    steps.put(sp.getName(), new Entry(sp, sp.disposeObservable().subscribe(p -> steps.remove(p.getName()).token.dispose())));
    return sp;
  }

  public void display() {
    synchronized(steps) {
      steps.values().forEach(e -> e.progress.display());
    }
  }
  
  public void undisplay() {
    synchronized(steps) {
      steps.values().forEach(e -> e.progress.undisplay());
    }
  }
  
  private static class Entry {
    public final IProgressView progress;
    public final Disposable token;
    
    private Entry(IProgressView progress, Disposable token) {
      this.progress = progress;
      this.token = token;
    }
  }
}
