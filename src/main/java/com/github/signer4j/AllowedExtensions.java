package com.github.signer4j;
import javax.swing.filechooser.FileNameExtensionFilter;

public interface AllowedExtensions {
  
  public static final FileNameExtensionFilter LIBRARIES = new FileNameExtensionFilter(
      "Biblioteca PKCS11 do Dispositivo (*.dll,*.dylib,*.so)", "dll", "dylib", "so", "so.1", "so.1.0.0", "so.2", "so.3", "so.3.0", "so.4", "so.8", "so.8.0", "so.1.2.2");
  
  public static final FileNameExtensionFilter CERTIFICATES = new FileNameExtensionFilter(
      "Arquivo PKCS12 (*.p12,*.pfx)", "p12", "pfx");
}
