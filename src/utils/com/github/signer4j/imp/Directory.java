package com.github.signer4j.imp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public abstract class Directory {
  private Directory() {}
  
  public static void clean(Path path) throws IOException {
    try (Stream<Path> walk = Files.walk(path)) {        
      walk.sorted(Comparator.reverseOrder())
        .map(Path::toFile)
        .peek(System.out::println)
        .forEach(File::delete);
    }
  }
}
