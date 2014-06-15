package com.damon.ds.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadExecutorFactory {

	public static ThreadPoolExecutor create(int size, int maxSize, int aliveSize, final String prefix) {
		BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
		return new ThreadPoolExecutor(size, maxSize, aliveSize, TimeUnit.SECONDS, workQueue,
				new DSThreadFactory(prefix));
	}

	public static ThreadPoolExecutor create() {
		return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
				new DSThreadFactory());
	}

	private static class DSThreadFactory implements ThreadFactory {
		private final AtomicInteger count = new AtomicInteger(1);
		private String prefix = "ThreadExecutorFactory";

		public DSThreadFactory() {

		}

		public DSThreadFactory(String prefix) {
			if (prefix != null && prefix.length() > 0) {
				this.prefix = prefix;
			}
		}

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, prefix + " #" + count.getAndIncrement());
		}
	}
}
