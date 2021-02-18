package com.github.signer4j.imp;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.github.signer4j.ICertificate;
import com.github.signer4j.ICertificates;
import com.github.signer4j.IToken;

final class Unavailables {
  private Unavailables() {}
  
  static ICertificates getCertificates(final IToken token) {
    return new ICertificates() {
      @Override
      public IToken getToken() {
        return token;
      }

      @Override
      public List<ICertificate> toList() {
        return Collections.emptyList();
      }

      @Override
      public Iterator<ICertificate> iterator() {
        return Collections.<ICertificate>emptyList().iterator();
      }

      @Override
      public int size() {
        return 0;
      }
    };
  };
}
