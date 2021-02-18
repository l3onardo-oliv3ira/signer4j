package com.github.signer4j.imp;

public class Threads {
	private Threads(){}
	
	public static void sleep(long time){
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
		long done = 0;
		do {
			long before = System.currentTimeMillis();
			Thread.sleep(millis - done);
			long after = System.currentTimeMillis();
			done += (after - before);
		} while (done < millis);
	}

  public static void async(Runnable runnable) {
    if (runnable != null) {
      new Thread(runnable).start();
    }
  }
}
