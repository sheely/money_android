package com.damon.ds.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadExecutorFactory {

	public static ThreadPoolExecutor create(int size, int maxSize,
			int aliveSize, final String prefix) {
		BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();

		ThreadFactory threadFactory = new ThreadFactory() {
			private final AtomicInteger mCount = new AtomicInteger(1);

			public Thread newThread(Runnable r) {
				return new Thread(r, prefix + " #" + mCount.getAndIncrement());
			}
		};

		return new ThreadPoolExecutor(size, maxSize, aliveSize,
				TimeUnit.SECONDS, workQueue, threadFactory);
	}
	
	public static ThreadPoolExecutor create(){
		return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
	}
}
