package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FileStrategy extends AbstractStrategy implements IDriverLookupStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileStrategy.class);
  
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
        createAndVisit(Paths.get(line), visitor);
      }
    } catch (IOException e) {
      LOGGER.debug("Exceção durante a leitura de lib's em " + file.getAbsolutePath(), e);
    }
  }
}
