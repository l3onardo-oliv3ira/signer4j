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
import java.security.Provider;
import java.security.Security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.function.ICreator;

final class SunPKCS11Creator {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(SunPKCS11Creator.class);
  
  private static final String SUN_PKCS11_PROVIDERNAME = "SunPKCS11";
  
  private static final String SUN_PKCS11_CLASS_NAME = "sun.security.pkcs11.SunPKCS11";
  
  private static Class<?> SUN_PKCS11_PROVIDER_CLASS;
  
  private SunPKCS11Creator() {}
  
  public static AuthProvider create(String configuration) throws UnavailableProviderException {
    Args.requireText(configuration, "configuration is empty");
    Method configure;
    if ((configure = isJavaGreaterOrEquals9()) != null)
      return createProviderJavaGreaterOrEquals9(configure, configuration);
    return createProviderJavaLowerThan9(configuration);
  }
  
  private static AuthProvider createProviderJavaLowerThan9(String configuration) throws UnavailableProviderException {
    return createProvider(configuration, (config) -> {
      try (ByteArrayInputStream bais = new ByteArrayInputStream(config.getBytes())) {
        return (AuthProvider)getProviderClass().getConstructor(InputStream.class).newInstance(bais);
      } catch(Exception e) {
        LOGGER.warn("Incapaz de instanciar PKCS11 (JDK < 9): " + SUN_PKCS11_PROVIDERNAME + " config: " + config, e);
        throw e;
      }
    });
  }
  
  private static AuthProvider createProviderJavaGreaterOrEquals9(Method configureMethod, String configuration)
    throws UnavailableProviderException {
    return createProvider(configuration, (config) -> {
      try {
        Provider provider = Security.getProvider(SUN_PKCS11_PROVIDERNAME);
        if (provider == null) {
          throw new UnavailableProviderException(
            "JRE aparentemente corrompida porque não foi publicada instância " + 
            SUN_PKCS11_PROVIDERNAME + " em java.security.Security. O sistema " + 
            "exige a dependência sunpkcs11.jar"
          );
        }
        // "--" is permitted in the constructor sun.security.pkcs11.Config
        return (AuthProvider)configureMethod.invoke(provider, "--" + config); 
      } catch (Exception e) {
        LOGGER.warn("Unable to instantiate PKCS11 (JDK >= 9): " + SUN_PKCS11_PROVIDERNAME + " config: " + config, e);
        throw e;
      }
    });
  }

  private static Method isJavaGreaterOrEquals9() throws UnavailableProviderException {
    try {
      return getProviderClass().getMethod("configure", String.class);
    } catch (NoSuchMethodException e) {
      return null; //Java Lower Than 9 does NOT have configure method!
    } catch (ClassNotFoundException e) {
      LOGGER.warn("JRE aparentemente corrompida porque não foi possível encontrar a class da dependência sunpkcs11.jar", e);
      throw new UnavailableProviderException(e);
    }
  }

  private static Class<?> getProviderClass() throws ClassNotFoundException {
    if (SUN_PKCS11_PROVIDER_CLASS == null)
      SUN_PKCS11_PROVIDER_CLASS = Class.forName(SUN_PKCS11_CLASS_NAME);
    return SUN_PKCS11_PROVIDER_CLASS;
  }
  
  private static AuthProvider createProvider(String configString, ICreator<String, AuthProvider, ?> creator) 
    throws UnavailableProviderException {
    String[] configAttempts = new String[] {
      configString + "\n\rdestroyTokenAfterLogout = true\n\r",
      configString
    };
    Throwable fail = null;
    for(String config: configAttempts) {
      try {
        return creator.create(config);
      } catch (Throwable e) {
        if (fail == null) {
          fail = e;
        } else {
          fail.addSuppressed(e);
        }
      }
    }
    throw new UnavailableProviderException("Unable to instantiate PKCS11 provider", fail);
  }
}
