package com.github.signer4j.imp;

import java.nio.charset.Charset;

public abstract class Constants {
  public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
  public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
  private Constants() {}  
}
