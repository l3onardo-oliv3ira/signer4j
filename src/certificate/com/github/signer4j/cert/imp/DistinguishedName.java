package com.github.signer4j.cert.imp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import com.github.signer4j.cert.IDistinguishedName;

class DistinguishedName implements IDistinguishedName {

  private String fullName;
  
  private final Properties properties = new Properties();

  public DistinguishedName(String fullName){
    this.fullName = fullName;
    this.setup();
  }
  
  private void setup() {
    try(ByteArrayInputStream s = new ByteArrayInputStream(fullName.replaceAll(",", "\n").getBytes())){
        properties.load(s);
    } catch (IOException e) {
      e.printStackTrace(); //we have to go back here!
    }
  }

  @Override
  public final String getFullName() {
    return fullName;
  }

  @Override
  public final Optional<String> getProperty(String key) {
    return Optional.ofNullable(properties.getProperty(key));
  }
  
  @Override
  public String toString() {
    return fullName;
  }
}
