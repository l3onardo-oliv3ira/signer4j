package com.github.signer4j.imp;

import static com.github.signer4j.imp.Streams.closeQuietly;
import static java.nio.file.Files.createTempFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.IDownloadStatus;

public class DownloadStatus implements IDownloadStatus {

  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadStatus.class);
  
  private File file;
  
  private OutputStream out;
  
  @Override
  public OutputStream onNewTry(int attemptCount) throws IOException {
    LOGGER.debug("Tentativa {} de download", attemptCount);
    return out = new BufferedOutputStream(new FileOutputStream(file = createTempFile("downloaded_tmp", ".signer4j.tmp").toFile()));
  }

  @Override
  public void onStartDownload(long total) {
    LOGGER.info("Iniciando o download. Tamanho do arquivo: {}", total);
  }

  @Override
  public void onStatus(long total, long written) {
    double percent = 100D * written / total;
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Baixados %.2f%%\"", percent);
    }
  }

  @Override
  public void onDownloadFail(Throwable e) {
    if (out != null) {
      closeQuietly(out);
      file.delete(); //temp file is deleted
      out = null;
      file = null;
    }
  }

  @Override
  public void onEndDownload() {
    if (out != null) {
      closeQuietly(out);
      out = null;
    }
  }
  
  public final File getDownloadedFile() {
    return file;
  }
}
