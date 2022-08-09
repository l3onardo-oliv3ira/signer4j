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


package com.github.signer4j.provider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.AuthProvider;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;

final class SunPKCS11Creator {
  
  private static final String SUN_PKCS11_PROVIDERNAME = "SunPKCS11";
  
  private static final String SUN_PKCS11_CLASS_NAME = "sun.security.pkcs11.SunPKCS11";
  
  private static Class<?> SUN_PKCS11_PROVIDER_CLASS;
  
  private SunPKCS11Creator() {}
  
  public static AuthProvider create(String configString) {
    Method configureMethod;
    if ((configureMethod = isJavaGreaterOrEquals9()) != null)
      return createProviderJavaGreaterOrEquals9(configureMethod, configString);
    return createProviderJavaLowerThan9(configString);
  }
  
  private static AuthProvider createProviderJavaLowerThan9(String configString) {
    try (ByteArrayInputStream bais = new ByteArrayInputStream(configString.getBytes())) {
      return (AuthProvider)SUN_PKCS11_PROVIDER_CLASS.getConstructor(InputStream.class).newInstance(bais);
    } catch (Exception e) {
      throw new RuntimeException("Unable to instantiate PKCS11 (JDK < 9) ", e);
    }
  }

  private static AuthProvider createProviderJavaGreaterOrEquals9(Method configureMethod, String configString) {
    try {
      Provider provider = Security.getProvider(SUN_PKCS11_PROVIDERNAME);
      // "--" is permitted in the constructor sun.security.pkcs11.Config
      return (AuthProvider)configureMethod.invoke(provider, "--" + configString);
    } catch (Exception e) {
      throw new RuntimeException("Unable to instantiate PKCS11 (JDK >= 9)", e);
    }
  }
  
  private static Method isJavaGreaterOrEquals9() {
    try {
      return getProviderClass().getMethod("configure", String.class);
    } catch (NoSuchMethodException e) {
      return null; //Java Lower Than 9
    } catch (NoSuchProviderException e) {
      throw new RuntimeException(e);
    }
  }

  private static Class<?> getProviderClass() throws NoSuchProviderException {
    if (SUN_PKCS11_PROVIDER_CLASS == null) {
      synchronized(SunPKCS11Creator.class) {
        if (SUN_PKCS11_PROVIDER_CLASS == null) { //double checking singleton
          try {
            SUN_PKCS11_PROVIDER_CLASS = Class.forName(SUN_PKCS11_CLASS_NAME);
          } catch (ClassNotFoundException e) {
            throw new NoSuchProviderException("Provider " + SUN_PKCS11_PROVIDERNAME + " not found");
          }
        }
      }
    }
    return SUN_PKCS11_PROVIDER_CLASS;
  }
}
