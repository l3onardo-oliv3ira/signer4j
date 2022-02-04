package com.github.signer4j;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public interface IDownloadStatus {
  
  OutputStream onNewTry(int attemptCount) throws IOException;
  
  void onStartDownload(long total);
  
  void onEndDownload();
  
  void onStatus(long total, long written);

  void onDownloadFail(Throwable e);

  Optional<File> getDownloadedFile();
}
