package com.github.signer4j;

import com.github.signer4j.imp.IChoice;
import com.github.signer4j.imp.exception.KeyStoreAccessException;

public interface ICertificateChooser {
  IChoice choose() throws KeyStoreAccessException ;
}
