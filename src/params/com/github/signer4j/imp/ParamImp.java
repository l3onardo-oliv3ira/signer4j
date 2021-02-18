package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireText;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.signer4j.IParam;

class ParamImp implements IParam {

  static final IParam NULL = new ParamImp(null);
  
  static ParamImp of(String name, Object value) {
    return new ParamImp(name, value);
  }
  
  private final String name;
  
  private final Optional<Object> value;
  
  private ParamImp(Object value) {
    this.name = null;
    this.value = Optional.ofNullable(value);
  }

  private ParamImp(String name, Object value) {
    this.name = requireText(name, "name can't be null").trim();
    this.value = Optional.ofNullable(value);
  }
  
  @Override
  public final String getName() {
    return name;
  }
  
  @Override
  public final <T> T get() {
    return orElse(null);
  }

  @SuppressWarnings("unchecked")
  @Override
  public final <T> T orElse(T defaultIf) {
    try {
      return (T)value.orElse(defaultIf);
    } catch(Throwable e) {
      return defaultIf;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <X extends Throwable, T> T orElseThrow(Supplier<? extends X> supplier) throws X {
    try {
      return (T)value.orElseThrow(supplier);
    } catch(Throwable e) {
      Throwable t = supplier.get();
      t.addSuppressed(e);
      throw (X)t;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T orElseGet(Supplier<? extends T> other) {
    try {
      return (T)value.orElseGet(other);
    }catch(Throwable e) {
      return other.get();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> void ifPresent(Consumer<T> consumer) {
    value.ifPresent((Consumer<Object>)consumer);      
  }

  @Override
  public boolean isPresent() {
    return value.isPresent();
  }
}