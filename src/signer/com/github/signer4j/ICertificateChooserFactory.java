package com.github.signer4j;

import java.util.function.BiFunction;

public interface ICertificateChooserFactory extends BiFunction<IKeyStoreAccess, ICertificates, ICertificateChooser> {
}
