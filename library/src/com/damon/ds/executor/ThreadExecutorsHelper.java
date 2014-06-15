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

	private static final int poolSize;
	private static final ThreadFactory concurrentThreadFactory;
	private static final ExecutorService executorService;
	private static final ScheduledExecutorService scheduledExecutorService;

	static {
		poolSize = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
		concurrentThreadFactory = new DSThreadFactory();
		executorService = Executors.newFixedThreadPool(poolSize, concurrentThreadFactory);
		scheduledExecutorService = Executors.newScheduledThreadPool(poolSize, concurrentThreadFactory);
	}

	public static Future<?> execute(Runnable runnable) {
		return executorService.submit(runnable);
	}

	public static ScheduledFuture<?> schedule(Callable<?> callable, long delay, TimeUnit unit) {
		return scheduledExecutorService.schedule(callable, delay, unit);
	}

	public static ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit unit) {
		return scheduledExecutorService.schedule(runnable, delay, unit);
	}

	/**
	 * 
	 * @param runnable
	 * @param delay
	 *            MILLISECONDS
	 */
	public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long delay) {
		return scheduleWithFixedDelay(runnable, delay, delay, TimeUnit.MILLISECONDS);
	}

	public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay,
			TimeUnit unit) {
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
		return scheduleAtFixedRate(runnable, period, period, TimeUnit.MILLISECONDS);
	}

	public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period,
			TimeUnit unit) {
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
