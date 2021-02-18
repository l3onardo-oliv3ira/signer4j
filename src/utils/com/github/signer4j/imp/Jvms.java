package com.github.signer4j.imp;

public class Jvms {
  private Jvms() {}

  public static String OS_NAME = System.getProperty("os.name").toLowerCase();

  public static final String SYSTEM_ROOT = computeSystemRoot();

  public static final String JAVA_VERSION = computeJavaVersion();

  public static final int JAVA_MAJOR_VERSION = computeJavaMajorVersion();

  public static final int JAVA_UPDATE_VERSION = computeJavaUpdateVersion();
  
  public static boolean isWindows() {
    return (OS_NAME.indexOf("win") >= 0);
  }
  
  public static boolean isMac() {
    return (OS_NAME.indexOf("mac") >= 0);
  }
  
  public static boolean isUnix() {
    return (OS_NAME.indexOf("nix") >= 0 || OS_NAME.indexOf("nux") >= 0 || OS_NAME.indexOf("aix") > 0);
  }
  
  public static boolean isSolaris() {
    return (OS_NAME.indexOf("sunos") >= 0);
  }
  
  private static String computeSystemRoot() {
    String value = System.getenv("SystemRoot");
    if (value == null)
      return Strings.empty();
    return value.replaceAll("\\\\", "/");
  }

  private static String computeJavaVersion() {
    return System.getProperty("java.version", "");
  }

  private static int computeJavaMajorVersion() {
    int version = -1;
    String full = System.getProperty("java.version", "");
    if (full.startsWith("1."))
      version = Strings.toInt(Character.toString(full.charAt(2)), -1);
    if (version > 0)
      return version;
    return Strings.toInt(System.getProperty("java.specification.version", "10"), 10);
  }

  private static int computeJavaUpdateVersion() {
    String full = System.getProperty("java.version");
    int index = full.indexOf('_');
    if (index != -1) {
      full = full.substring(index + 1);
      return Integer.parseInt(full);
    }
    return 0;
  }
}
