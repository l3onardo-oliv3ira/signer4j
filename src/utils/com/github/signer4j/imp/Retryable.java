package com.github.signer4j.imp;

public final class Retryable {
  
  public static void attempt(long interval, long timeout, Executable exec) throws RetryTimeoutException, Exception{
    attempt(0L, interval, timeout, exec);
  }
  
  public static void attempt(long delay, long interval, long timeout, Executable exec) throws RetryTimeoutException, Exception{
    new Retryable(exec).execute(delay, interval, timeout);
  }

  @FunctionalInterface
  public static interface Executable {
    void execute() throws TemporaryException, Exception;
  }
  
  private final Executable executable;
  
  private Retryable(Executable executable) {
    this.executable = executable;
  }

	private void execute(long delay, long interval, long timeout) throws RetryTimeoutException, Exception {
		long start = System.currentTimeMillis();
		if (delay > 0L)
			Threads.sleep(delay);
		do {
			try {
				executable.execute();
				return;
			} catch (TemporaryException e) {
				if (System.currentTimeMillis() - start < timeout)
					Threads.sleep(interval);
				else
					throw new RetryTimeoutException(e);
			}
		} while (true);
	}
}