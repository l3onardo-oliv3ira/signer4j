package com.github.signer4j.imp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.signer4j.IFilePath;

public class FilePath implements IFilePath {
  
  private final Path path;
  
  public FilePath(Path path) {
    this.path = Args.requireNonNull(path, "path is null");
  }
  
  @Override
  public final String getPath() {
    return path.toFile().getAbsolutePath();
  }
  
  @Override
  public final String toString() {
    return getPath();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((path == null) ? 0 : path.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FilePath other = (FilePath) obj;
    if (path == null) {
      if (other.path != null)
        return false;
    } else {
      try {
        if (!Files.isSameFile(path, other.path))
          return false;
      } catch (IOException e) {
        return false;
      }
    }
    return true;
  }
}
