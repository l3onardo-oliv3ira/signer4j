package com.github.signer4j;

import java.util.function.BiFunction;

import com.github.signer4j.imp.ConsoleChooser;
import com.github.signer4j.imp.DefaultChooser;

public interface ICertificateChooserFactory extends BiFunction<IKeyStoreAccess, ICertificates, ICertificateChooser> {
  static ICertificateChooserFactory CONSOLE = (k, c) -> new ConsoleChooser(k, c);
  static ICertificateChooserFactory DEFAULT = (k, c) -> new DefaultChooser(k, c);
}
