package com.damon.ds.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadExecutorsHelper {

	private static int poolSize;
	private static ThreadFactory concurrentThreadFactory;
	private static ExecutorService executorService;
	private static ScheduledExecutorService scheduledExecutorService;

	static {
		poolSize = Math.max(2, Runtime.getRuntime().availableProcessors());
		concurrentThreadFactory = new DSThreadFactory();
		init();
	}

	private static void init() {
		if (executorService == null || executorService.isShutdown()) {
			executorService = Executors.newFixedThreadPool(poolSize, concurrentThreadFactory);
			scheduledExecutorService = Executors.newScheduledThreadPool(poolSize, concurrentThreadFactory);
		}
	}

	public static void shutdown() {
		if (executorService != null && !executorService.isShutdown()) {
			executorService.shutdown();
			scheduledExecutorService.shutdown();
		}
	}

	public static Future<?> execute(Runnable runnable) {
		init();
		return executorService.submit(runnable);
	}

	public static ScheduledFuture<?> schedule(Callable<?> callable, long delay, TimeUnit unit) {
		init();
		return scheduledExecutorService.schedule(callable, delay, unit);
	}

	/**
	 * 
	 * @param runnable
	 * @param delay
	 *            MILLISECONDS
	 * @return
	 */
	public static ScheduledFuture<?> schedule(Runnable runnable, long delay) {
		init();
		return schedule(runnable, delay, TimeUnit.MILLISECONDS);
	}

	public static ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit unit) {
		init();
		return scheduledExecutorService.schedule(runnable, delay, unit);
	}

	/**
	 * 
	 * @param runnable
	 * @param delay
	 *            MILLISECONDS
	 */
	public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long delay) {
		init();
		return scheduleWithFixedDelay(runnable, delay, delay, TimeUnit.MILLISECONDS);
	}

	public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay,
			TimeUnit unit) {
		init();
		return scheduledExecutorService.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
	}

	/**
	 * 
	 * @param runnable
	 * @param period
	 *            MILLISECONDS
	 * @return
	 */
	public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long period) {
		init();
		return scheduleAtFixedRate(runnable, period, period, TimeUnit.MILLISECONDS);
	}

	public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period,
			TimeUnit unit) {
		init();
		return scheduledExecutorService.scheduleAtFixedRate(runnable, initialDelay, period, unit);
	}

	private static class DSThreadFactory implements ThreadFactory {
		private final AtomicInteger count = new AtomicInteger(1);;

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "AsyncTask #" + count.getAndIncrement());
		}
	}
}
