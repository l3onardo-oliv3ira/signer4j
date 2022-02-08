package com.github.signer4j.imp;

import java.nio.charset.Charset;

public abstract class Constants {
  public static final Charset UTF_8 = Charset.forName("UTF-8");
  public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
  public static final Charset DEFAULT_CHARSET = UTF_8;
  private Constants() {}  
}
