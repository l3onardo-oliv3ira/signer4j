package com.github.signer4j.imp;

import static com.github.signer4j.imp.Streams.closeQuietly;
import static java.nio.file.Files.createTempFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.IDownloadStatus;

public class DownloadStatus implements IDownloadStatus {

  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadStatus.class);
  
  private File file;
  
  private boolean online = false;
  
  private final boolean rejectEmpty;
  
  private OutputStream out;
  
  public DownloadStatus() {
    this(false);
  }

  public DownloadStatus(boolean rejectEmpty) {
    this.rejectEmpty = rejectEmpty;
  }

  private void checkIfOffline() {
    throwIfOnlineIs(true, "status is online");
  }
  
  private void checkIfOnline() {
    throwIfOnlineIs(false, "status is offline");
  }

  private void throwIfOnlineIs(boolean status, String message) {
    if (online == status) {
      throw new IllegalStateException(message);
    }
  }

  @Override
  public OutputStream onNewTry(int attemptCount) throws IOException {
    checkIfOffline();
    LOGGER.debug("Tentativa {} de download", attemptCount);
    out = new FilterOutputStream(new FileOutputStream(file = createTempFile("downloaded_tmp", ".signer4j.tmp").toFile())) {
      @Override
      public void close() throws IOException {
        try {
          super.close();
        }finally {
          DownloadStatus.this.out = null;
          DownloadStatus.this.checkIfEmpty(false);
          DownloadStatus.this.online = false;
        }
      }
    };
    this.online = true;
    return out;
  }

  @Override
  public void onStartDownload(long total) {
    checkIfOnline();
    LOGGER.info("Iniciando o download. Tamanho do arquivo: {}", total);
  }

  @Override
  public void onStatus(long total, long written) {
    checkIfOnline();
    double percent = 100D * written / total;
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Baixados %.2f%%\"", percent);
    }
  }

  @Override
  public void onDownloadFail(Throwable e)  {
    onEndDownload();
    checkIfEmpty(true);
  }

  @Override
  public void onEndDownload() {
    checkIfOnline();
    closeQuietly(out);
  }

  private void checkIfEmpty(boolean force) {
    if (file != null) {
      if (force || (rejectEmpty && file.length() == 0)) {
        file.delete();
        file = null;
      }
    }
  }
  
  @Override
  public final Optional<File> getDownloadedFile() {
    checkIfOffline();
    return Optional.ofNullable(file);
  }
}
