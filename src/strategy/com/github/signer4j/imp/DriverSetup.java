package com.github.signer4j.imp;

import static com.github.utils4j.imp.Args.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import com.github.signer4j.IDriverSetup;
import com.github.utils4j.imp.Streams;

class DriverSetup implements IDriverSetup {
  
  public static Optional<DriverSetup> create(Path library) {
    try {
      return Optional.of(new DriverSetup(library.toAbsolutePath()));
    }catch(IOException e) {
      return Optional.empty();
    }
  }
  
  private final String md5;

  private final Path library;
  
  private DriverSetup(Path library) throws IOException {
    this.library = requireNonNull(library, "Unabled to create driversupport with null library");
    this.md5 = Streams.checkMd5Sum(library.toFile());
  }
  
  @Override
  public final Path getLibrary() {
    return library;
  }
  
  @Override
  public final String getMd5() {
    return md5;
  }

  @Override
  public final int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((md5 == null) ? 0 : md5.hashCode());
    return result;
  }

  @Override
  public final boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DriverSetup other = (DriverSetup) obj;
    if (md5 == null) {
      if (other.md5 != null)
        return false;
    } else if (!md5.equals(other.md5))
      return false;
    return true;
  }
}
