package com.github.signer4j.imp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Services {
  private static final Logger LOGGER = LoggerFactory.getLogger(Services.class);
  
  private Services() {
  }

  public static void shutdownNow(ExecutorService pool) {
    shutdownNow(pool, 5);
  }

  public static void shutdownNow(ExecutorService pool, long timeoutSec) {
    if (pool != null) {
      LOGGER.debug("Impedindo que novas tarefas sejam submetidas");
      /** Impede que novas tarefas sejam submetidas **/
      pool.shutdown();
      LOGGER.debug("Cancelando tarefas atualmente em execução");
      /** Cancela tarefas atualmente rodando **/
      pool.shutdownNow();
      doShutdown(pool, timeoutSec);
    }
  }

  public static void shutdown(ExecutorService pool) {
    if (pool != null) {
      /** Impede que novas tarefas sejam submetidas **/
      pool.shutdown();
      doShutdown(pool, 60);
    }
  }

  private static void doShutdown(ExecutorService pool, long timeout) {
    boolean shutdown = false;
    do{
      try {
        LOGGER.debug("Aguardando enquanto tiver tarefas sendo finalizadas por " + timeout + " segundos");
        /** Aguarda enquanto estiver tarefas sendo finalizadas **/
        shutdown = pool.awaitTermination(timeout, TimeUnit.SECONDS);
      } catch (InterruptedException ie) {
        LOGGER.debug("Capturada InterruptedException");
        /** Preserva o status de interrupção corrent **/
        Thread.currentThread().interrupt();
      }
      if (!shutdown) {
        LOGGER.debug("Novo pedido de cancelamento de tarefas em execução");
        /** Cancela tarefas corrententemente rodando **/
        pool.shutdownNow();
      }
    }while (!shutdown);
  }
}
