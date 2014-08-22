package com.xdamon.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;

/**
 * 防止应用崩溃
 * <p>
 * 调用SafeLooper.install()，主消息循环会被接管，所有的消息会运行在一个嵌套的子消息循环中<br>
 * 一旦崩溃，getUncaughtExceptionHandler()将会接收到崩溃的消息，但是主消息循环会继续执行。
 * <p>
 * 注意SafeLooper不会处理背景线程的异常
 * 
 * @author yimin.tu
 * 
 */
public class SafeLooper implements Runnable {
	private static final Object EXIT = new Object();
	private static boolean installed;
	private static Handler handler = new Handler(Looper.getMainLooper());
	private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

	/**
	 * 在下一个消息循环生效
	 */
	public static void install() {
		handler.removeMessages(0, EXIT);
		handler.post(new SafeLooper());
	}

	/**
	 * 在millis后退出
	 */
	public static void uninstallDelay(long millis) {
		handler.removeMessages(0, EXIT);
		handler.sendMessageDelayed(handler.obtainMessage(0, EXIT), millis);
	}

	/**
	 * 在下一个消息循环失效
	 */
	public static void uninstall() {
		uninstallDelay(0);
	}

	public static Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
		return uncaughtExceptionHandler;
	}

	public static void setUncaughtExceptionHandler(
			Thread.UncaughtExceptionHandler h) {
		uncaughtExceptionHandler = h;
	}

	@Override
	public void run() {
		if (installed)
			return;
		if (Looper.myLooper() != Looper.getMainLooper())
			return;

		Method next;
		Field target;
		try {
			Method m = MessageQueue.class.getDeclaredMethod("next");
			m.setAccessible(true);
			next = m;
			Field f = Message.class.getDeclaredField("target");
			f.setAccessible(true);
			target = f;
		} catch (Exception e) {
			return;
		}

		installed = true;
		MessageQueue queue = Looper.myQueue();
		Binder.clearCallingIdentity();
		final long ident = Binder.clearCallingIdentity();

		while (true) {
			try {
				Message msg = (Message) next.invoke(queue);
				if (msg == null || msg.obj == EXIT)
					break;
				// Log.i("loop", String.valueOf(msg));

				Handler h = (Handler) target.get(msg);
				h.dispatchMessage(msg);
				final long newIdent = Binder.clearCallingIdentity();
				if (newIdent != ident) {
				}
				msg.recycle();
			} catch (Exception e) {
				Thread.UncaughtExceptionHandler h = uncaughtExceptionHandler;
				Throwable ex = e;
				if (e instanceof InvocationTargetException) {
					ex = ((InvocationTargetException) e).getCause();
					if (ex == null) {
						ex = e;
					}
				}
				e.printStackTrace(System.err);
				if (h != null) {
					h.uncaughtException(Thread.currentThread(), ex);
				}
				new Handler().post(this);
				break;
			}
		}

		installed = false;
	}
}
