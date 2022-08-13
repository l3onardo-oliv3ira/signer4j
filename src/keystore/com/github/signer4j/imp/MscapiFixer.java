/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package com.github.signer4j.imp;

import java.lang.reflect.Field;
import java.security.KeyStore;
import java.security.KeyStoreSpi;
import java.security.cert.X509Certificate;
import java.util.Collection;

import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Containers;

enum MscapiFixer implements IKeyStoreBugFixer {
  /**
   * https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6672015
   * https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6483657
   */
  BUG_6483657() {
    
    @Override
    public void fix(KeyStore keyStore) throws Signer4JException {
      try {
        Field field = keyStore.getClass().getDeclaredField("keyStoreSpi");
        field.setAccessible(true);
        Object fieldValue = field.get(keyStore);
        if (!isCompatible(fieldValue, KeyStoreSpi.class)) {
          return;
        }
        final KeyStoreSpi ksspi = (KeyStoreSpi)fieldValue;
        if ("sun.security.mscapi.KeyStore$MY".equals(ksspi.getClass().getName())) {
          field = ksspi.getClass().getEnclosingClass().getDeclaredField("entries");
          field.setAccessible(true);
          fieldValue = field.get(ksspi);
          if (!isCompatible(fieldValue, Collection.class)) {
            return;
          }
          final Collection<?> entries = (Collection<?>)fieldValue;
          for (final Object entry : entries) {
            field = entry.getClass().getDeclaredField("certChain");
            field.setAccessible(true);
            fieldValue = field.get(entry);
            if (!isCompatible(fieldValue, X509Certificate[].class)) {
              return;
            }
            final X509Certificate[] certificates = (X509Certificate[])fieldValue;
            if (Containers.isEmpty(certificates))
              return;
            final String hashCode = Integer.toString(certificates[0].hashCode());
            field = entry.getClass().getDeclaredField("alias");
            field.setAccessible(true);
            fieldValue = field.get(entry);
            if (!isCompatible(fieldValue, String.class)) {
              return;
            }
            final String localAlias = (String)fieldValue;
            if (!localAlias.equals(hashCode)) {
              final String newAlias = localAlias.concat(" - ").concat(hashCode);
              field.set(entry, newAlias);
            }
          }
        }
      } catch (Exception e) {
        throw new Signer4JException("Unabled to fix aliases on mscapi keystore", e);
      }
    }
  };
  
  private static boolean isCompatible(final Object instance, final Class<?> targetClass) {
    return instance == null || targetClass.isInstance(instance);
  }
}
