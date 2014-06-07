package com.next.util;

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
public class MainUILooperDelegate implements Runnable {
	private static boolean installed;
	private static boolean exit;
	private static Handler handler = new Handler(Looper.getMainLooper());
	private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

	/**
	 * 在下一个消息循环生效
	 */
	public static void install() {
		exit = false;
		handler.post(new MainUILooperDelegate());
	}
	
	public static boolean isInstalled(){
		return installed;
	}

	/**
	 * 在下一个消息循环失效
	 */
	public static void uninstall() {
		exit = true;
		handler.sendEmptyMessage(0);
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
			if (exit) {
				exit = false;
				break;
			}
			try {
				Message msg = (Message) next.invoke(queue);
				if (msg == null)
					return;
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
