package com.github.signer4j.imp;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.codec.digest.DigestUtils;

public class Streams {
  private Streams() {
  }

  public static void closeQuietly(Closeable c) {
    try {
      c.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void closeQuietly(AutoCloseable c) {
    try {
      c.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public static void closeQuietly(OutputStream s) {
    if (s != null) {
      flushQuietly(s);
      closeQuietly((AutoCloseable)s);
    }
  }

  public static void flushQuietly(OutputStream s) {
    if (s != null) {
      try {
        s.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  public static void consumeQuietly(InputStream s) {
    if (s != null) {
      try {
        byte[] buffer = new byte[512];
        while((s.read(buffer)) != -1)
          ;
      }catch(IOException e) {
        e.printStackTrace();
      }finally {
        closeQuietly(s);
      }
    }
  }

  public static CompletableFuture<String> readOutStream(InputStream is) {
    return readOutStream(is, Constants.DEFAULT_CHARSET);
  }

  public static CompletableFuture<String> readOutStream(InputStream is, Charset charset) {
    return CompletableFuture.supplyAsync(() -> {
      try (BufferedReader br = new BufferedReader(new InputStreamReader(is, charset))){
        StringBuilder res = new StringBuilder();
        String inputLine;
        while ((inputLine = br.readLine()) != null) {
          res.append(inputLine).append(System.lineSeparator());
        }
        return res.toString();
      } catch (IOException e) {
        throw new RuntimeException("problem with executing program", e);
      }
    });
  }

  public static String checkMd5Sum(File file) throws IOException {
    try(InputStream fis =  new FileInputStream(file)){
      return DigestUtils.md5Hex(fis);
    }
  }
  
  public static boolean isSame(Path path1, Path path2) {
    try {
      return Files.isSameFile(path2, path1);
    } catch (IOException e) {
      return false; //we have to go back here!
    }
  }
  
  public static boolean isSame(String path1, String path2) {
    return isSame(Paths.get(path1), Paths.get(path2));
  }
  
  public static byte[] fromResourceQuietly(String name) {
    try(InputStream is = Streams.class.getResourceAsStream(name)) {
      return fromStream(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static byte[] fromResource(String name) throws IOException {
    try(InputStream is = Streams.class.getResourceAsStream(name)) {
      return fromStream(is);
    }
  }
  
  public static byte[] fromStream(InputStream is) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    byte[] data = new byte[512];
    int read;
    while ((read = is.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, read);
    }
    return buffer.toByteArray();
  }
  
  public static void copy(byte[] data, OutputStream out) throws IOException {
    copy(data, out, 32 * 1024); 
  }

  public static void copy(byte[] data, OutputStream out, int bufferSize) throws IOException {
    try(InputStream input = new ByteArrayInputStream(data)) {
      copy(input, out, bufferSize);
    }
  }
  
  public static void copy(InputStream input, OutputStream output, int bufferSize) throws IOException {
    byte[] buffer = new byte[bufferSize];
    int read;
    while((read = input.read(buffer)) > 0) {
      output.write(buffer, 0, read);
    }
  }
  
}
