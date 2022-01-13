package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;
import static java.nio.file.Files.createTempFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.IDownloadStatus;
import com.github.signer4j.progress.IProgress;

public class DownloadStatus implements IDownloadStatus {

  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadStatus.class);
  
  private final IProgress progress;
  
  private File file;
  
  private OutputStream out;
  
  public DownloadStatus(IProgress progress) {
    this.progress = requireNonNull(progress, "progress is null");
  }

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
  public void onDownloadFail(Exception e) {
    Streams.closeQuietly(out);
    out = null;
    file.delete();
    file = null;
  }

  @Override
  public void onEndDownload() {
    Streams.closeQuietly(out); 
    progress.step("Download concluído");
  }
  
  public final File getDownloadedFile() {
    return file;
  }
}
