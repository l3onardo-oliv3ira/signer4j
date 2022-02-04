package com.github.signer4j.imp;

import static com.github.signer4j.imp.Throwables.tryRun;

import java.io.IOException;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

public class HttpTools {

  private static CloseableHttpClient TOUCH_CLIENT = HttpClientBuilder.create()
    .disableAuthCaching()
    .disableAutomaticRetries()
    .disableConnectionState()
    .disableContentCompression()
    .disableCookieManagement()
    .disableRedirectHandling()
    .evictExpiredConnections()
    .evictIdleConnections(TimeValue.ofMinutes(1))
    .setDefaultRequestConfig(RequestConfig.custom()
      .setCookieSpec(StandardCookieSpec.IGNORE).build())
    .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
        .setMaxConnPerRoute(2)
        .setMaxConnTotal(2)
        .setValidateAfterInactivity(TimeValue.ofSeconds(10))
        .build())
    .build();
      
  private HttpTools() {}

  public static void touch(String uri) throws IOException {
    touch(uri, "HttpTools"); 
  }

  public static void touchQuietly(String uri) {
    tryRun(() -> touch(uri, "HttpTools"));
  }
  
  public static void touch(String uri, String userAgent) throws IOException {
    touch(uri, userAgent, Timeout.ofSeconds(5));
  }

  public static void touch(String uri, String userAgent, Timeout timeout) throws IOException {
    Args.requireNonNull(uri, "uri is null");
    Args.requireNonNull(userAgent, "userAgent is null");
    Args.requireNonNull(timeout, "timeout is null");
    Request.get(uri).connectTimeout(timeout).responseTimeout(timeout).execute(TOUCH_CLIENT).discardContent();
  }
}
