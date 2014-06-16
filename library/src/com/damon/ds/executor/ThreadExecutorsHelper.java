package com.damon.ds.executor;

import java.util.HashMap;
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
	private static HashMap<Object, Future<?>> runnableMap;

	static {
		poolSize = Math.max(2, Runtime.getRuntime().availableProcessors());
		concurrentThreadFactory = new DSThreadFactory();
		runnableMap = new HashMap<Object, Future<?>>();
		init();
	}

	private static void init() {
		if (executorService == null || executorService.isShutdown()) {
			executorService = Executors.newFixedThreadPool(poolSize, concurrentThreadFactory);
			scheduledExecutorService = Executors.newScheduledThreadPool(poolSize, concurrentThreadFactory);
		}
	}

	public static void cancel(Runnable r, boolean myInterruptIfRunning) {
		cancel_(r, myInterruptIfRunning);
	}

	public static void cancel(Callable<?> c, boolean myInterruptIfRunning) {
		cancel_(c, myInterruptIfRunning);
	}

	private static void cancel_(Object obj, boolean myInterruptIfRunning) {
		Future<?> future = runnableMap.get(obj);
		if (future != null) {
			future.cancel(myInterruptIfRunning);
			runnableMap.remove(obj);
		}
	}

	public static void shutdownNow() {
		if (executorService != null && !executorService.isShutdown()) {
			executorService.shutdownNow();
			scheduledExecutorService.shutdownNow();
			runnableMap.clear();
		}
	}

	public static Future<?> execute(Runnable runnable) {
		init();
		Future<?> future = executorService.submit(runnable);
		runnableMap.put(runnable, future);
		return future;
	}

	public static ScheduledFuture<?> schedule(Callable<?> callable, long delay, TimeUnit unit) {
		init();
		ScheduledFuture<?> future = scheduledExecutorService.schedule(callable, delay, unit);
		runnableMap.put(callable, future);
		return future;
	}

	/**
	 * 
	 * @param runnable
	 * @param delay
	 *            MILLISECONDS
	 * @return
	 */
	public static ScheduledFuture<?> schedule(Runnable runnable, long delay) {
		return schedule(runnable, delay, TimeUnit.MILLISECONDS);
	}

	public static ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit unit) {
		init();
		ScheduledFuture<?> future = scheduledExecutorService.schedule(runnable, delay, unit);
		runnableMap.put(runnable, future);
		return future;
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
		init();
		ScheduledFuture<?> future = scheduledExecutorService
				.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
		runnableMap.put(runnable, future);
		return future;
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
		init();
		ScheduledFuture<?> future = scheduledExecutorService.scheduleAtFixedRate(runnable, initialDelay, period, unit);
		runnableMap.put(runnable, future);
		return future;
	}

	private static class DSThreadFactory implements ThreadFactory {
		private final AtomicInteger count = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "ThreadExecutorsHelper #" + count.getAndIncrement());
		}
	}
}
