package com.github.signer4j.imp;

import static com.github.signer4j.imp.Throwables.tryRun;

public class Threads {
  private Threads(){}
  
  public static void sleep(long time){
    if (time > 0)
      try {
        Thread.sleep(time);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
  }
  
  /**
   * Por alguma razao o metodo sleep da classe Thread retorna sem que 
   * seja passado efetivamente pelo periodo de tempo informado no parametro.
   * Este método garante que pelo menos 'millis' milisegundos tenham se 
   * passado antes do retorno, ou seja, pode se passar alguns milisegundos
   * a mais, mas nunca a menos. 
   * @param millis
   * @throws InterruptedException
   */
  public static void safeSleep(long millis) throws InterruptedException {
    if (millis == 0)
      return;
    long done = 0;
    do {
      long before = System.currentTimeMillis();
      Thread.sleep(millis - done);
      long after = System.currentTimeMillis();
      done += (after - before);
    } while (done < millis);
  }

  public static void async(Runnable runnable) {
    async("From:" + Thread.currentThread().getName(), runnable);
  }
  
  public static void async(String threadName, Runnable runnable) {
    if (runnable != null) {
      new Thread(runnable, threadName).start();
    }
  }
  
  public static ShutdownHookThread shutdownHook(Runnable runnable) {
    return shutdownHookAdd(runnable, "shutdownhook:" + Dates.stringNow());
  }
  
  public static ShutdownHookThread shutdownHookAdd(Runnable runnable, String name) {
    ShutdownHookThread t = new ShutdownHookThread(name, runnable);
    Runtime.getRuntime().addShutdownHook(t);
    return t;
  }

  public static void shutdownHookRem(ShutdownHookThread jvmHook) {
    if (!isShutdownHook()) {
      tryRun(() -> Runtime.getRuntime().removeShutdownHook(jvmHook), true);
    }
  }
  
  public static class ShutdownHookThread extends Thread {
    ShutdownHookThread(String name, Runnable r){
      super(r, name);
    }
  }

  public static boolean isShutdownHook() {
    return Thread.currentThread() instanceof ShutdownHookThread;
  }
}
