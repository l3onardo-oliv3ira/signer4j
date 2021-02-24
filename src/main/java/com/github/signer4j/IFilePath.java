package com.github.signer4j;
import static java.nio.file.Paths.get;

import java.nio.file.Path;
import java.util.List;

public interface IFilePath {

  String getPath();
  
  static Path[] toPaths(List<IFilePath> list) {
    return list.stream().map(fp -> get(fp.getPath())).toArray(Path[]::new);
  }
}
