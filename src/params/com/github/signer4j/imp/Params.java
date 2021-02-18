package com.github.signer4j.imp;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.signer4j.IParam;
import com.github.signer4j.IParams;

public class Params implements IParams {
  
  public static final Params EMPTY = new Params() {
    @Override
    public Params of(String name, Object value) {
      throw new IllegalStateException("Unabled to create param on EMPTY instance");
    }
  };
  
  public static Params create() {
    return new Params();
  }
  
  private static Params create(Map<String, IParam> map) {
    return new Params(new HashMap<String, IParam>(map));
  }
  
  private final Map<String, IParam> params;
  
  protected Params() {
    this(new HashMap<>());
  }
  
  private Params(HashMap<String, IParam> params) {
    this.params = params;
  }

  public Params of(String name, Optional<?>value) {
    params.put(name, ParamImp.of(name, value.orElse(null)));
    return this;
  }
  
  public Params of(String name, Object value) {
    params.put(name, ParamImp.of(name, value));
    return this;
  }

  public Params clone() {
    return Params.create(new HashMap<String, IParam>(this.params));
  }
  
  @Override
  public final IParam get(String key) {
    return Optional.ofNullable(params.get(key)).orElse(ParamImp.NULL);
  }
}
