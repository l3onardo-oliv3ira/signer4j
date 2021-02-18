package com.github.signer4j.imp;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpTools {

  private HttpTools() {}

  public static boolean isLocalHost(String ip) {
    return "0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip);
  }

  public static boolean sendGetRequest(String request) {
    return sendGetRequest(request, HttpTools.class.getSimpleName());
  }

  public static boolean sendGetRequest(String request, String userAgent) {
    return Throwables.tryRun(() -> {
      URL url = new URL(request);
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      try {
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", userAgent);
        int statusCode = connection.getResponseCode();
        Streams.consumeQuietly(connection.getInputStream());
        return statusCode == HttpURLConnection.HTTP_OK;
      }finally {
        connection.disconnect();
      }
    });
  }

  /**
   * A api HttpURLConnection faz novos retries silenciosamente.
   * A idéia deste método é que a requisição seja feita e, para evitar um retry
   * por um atraso na resposta do servidor, simplesmente disconecta 2 segundos
   * após
   * */
  public static void sendGetRequestAndDisconnect(String request, String userAgent) {
    Throwables.tryRun(() -> {
      URL url = new URL(request);
      final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("User-Agent", userAgent);
      Threads.async(() -> {
        Threads.sleep(2000);
        connection.disconnect();
      });
      connection.getResponseCode();
    }, true);
  }


  public static boolean sendGetRequest(String request, String userAgent, String jsonBody) {
    return Throwables.tryRun(() -> {
      URL url = new URL(request);
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      try {
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent", userAgent);
        try(OutputStream os = connection.getOutputStream()) {
          byte[] body = jsonBody.getBytes("utf-8");
          os.write(body, 0, body.length);     
        }
        int statusCode = connection.getResponseCode();
        Streams.consumeQuietly(connection.getInputStream());
        return statusCode == HttpURLConnection.HTTP_OK;
      }finally {
        connection.disconnect();
      }
    });
  }
}
