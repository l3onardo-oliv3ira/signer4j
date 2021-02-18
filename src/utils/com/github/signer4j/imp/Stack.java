package com.github.signer4j.imp;

import java.util.Iterator;
import java.util.LinkedList;

public class Stack<T> implements Iterable<T>{
  private LinkedList<T> list = new LinkedList<>();

  public void push(T x){
    list.add(x);
  }

  public T pop() {
    return list.removeLast(); 
  }

  public T peek() {
    return list.getLast();
  }

  public int size() {
    return list.size();
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

  @Override
  public Iterator<T> iterator() {
    return list.iterator();
  }

  public void clear() {
    this.list.clear();
  }
}
