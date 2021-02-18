package com.github.signer4j.imp;

import java.lang.reflect.Field;
import java.security.KeyStore;
import java.security.KeyStoreSpi;
import java.security.cert.X509Certificate;
import java.util.Collection;

import com.github.signer4j.imp.exception.KeyStoreAccessException;

enum MscapiFixer implements IKeyStoreBugFixer {
  /**
   * https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6672015
   * https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6483657
   */
  BUG_6483657() {
    private boolean affectedVersion() {
      return Jvms.JAVA_MAJOR_VERSION < 8 || (Jvms.JAVA_MAJOR_VERSION == 8 && Jvms.JAVA_UPDATE_VERSION < 101);
    }
    
    @Override
    public void fix(KeyStore keyStore) throws KeyStoreAccessException {
      if (!affectedVersion())
        return;
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
        throw new KeyStoreAccessException("Unabled to fix aliases on mscapi keystore", e);
      }
    }
  };
  
  private static boolean isCompatible(final Object instance, final Class<?> targetClass) {
    return instance == null || targetClass.isInstance(instance);
  }
}
