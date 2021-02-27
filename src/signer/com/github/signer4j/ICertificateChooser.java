package com.github.signer4j;

import com.github.signer4j.imp.IChoice;
import com.github.signer4j.imp.exception.Signer4JException;

public interface ICertificateChooser {
  IChoice choose() throws Signer4JException ;
}
