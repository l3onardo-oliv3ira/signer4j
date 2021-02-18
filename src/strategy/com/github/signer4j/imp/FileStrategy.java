package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

class FileStrategy extends ExceptionExpert implements IDriverLookupStrategy {

  private final File file;

  public FileStrategy(File file) {
    this.file = requireNonNull(file, "file can't be null");
  }

  @Override
  public void lookup(IDriverVisitor visitor) {
    try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while((line = reader.readLine()) != null) {
        line = Strings.trim(line);
        if (line.isEmpty())
          continue;
        Optional<DriverSetup> ds = DriverSetup.create(Paths.get(line));
        if (ds.isPresent()) {
          visitor.visit(ds.get());
        }
      }
    } catch (IOException e) {
      handleException(e);
    }
  }
}
