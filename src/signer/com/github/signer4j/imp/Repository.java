package com.github.signer4j.imp;

import static com.github.utils4j.imp.Strings.trim;

import com.github.signer4j.IRepository;

public enum Repository implements IRepository {
  
  MSCAPI("MSCAPI"),

  NATIVE("NATIVE");
  
  private final String name;
  
  public static Repository from(String name) {
    return name == null || NATIVE.name.equalsIgnoreCase(name = trim(name)) ? NATIVE : MSCAPI.name.equalsIgnoreCase(name) ? MSCAPI : Repository.NATIVE;
  }
  
  Repository(String name) {
    this.name = name;
  }

  @Override
  public final String getName() {
    return name;
  }
  
  public boolean isMSCAPI() {
    return this == MSCAPI;
  }
  
  public boolean isNative() {
    return this == NATIVE;
  }
}
