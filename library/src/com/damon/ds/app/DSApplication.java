package com.damon.ds.app;

import java.util.UUID;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.damon.ds.util.Log;

public class DSApplication extends Application {

	private static DSApplication instance;
	private String sessionId;

	public static DSApplication instance() {
		if (instance == null) {
			throw new IllegalStateException("Application has not been created");
		}

		return instance;
	}

	static DSApplication _instance() {
		return instance;
	}

	public DSApplication() {
		instance = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		if ((getApplicationInfo().flags & 2) != 0) {
			// ApplicationInfo.FLAG_DEBUGGABLE
			Log.LEVEL = Log.VERBOSE;
		} else {
			Log.LEVEL = Integer.MAX_VALUE;
		}

	}

	//
	// Application lifecycle management
	//

	/**
	 * Application的Activity堆栈第一次不为空时调用
	 * <p>
	 * 在第一个Activity.onCreate()调用之前被调用
	 */
	public void onApplicationStart() {
		Log.i("application", "onApplicationStart");

		sessionId = UUID.randomUUID().toString();
	}

	/**
	 * Application的Activity堆栈第一次为空时调用
	 * <p>
	 * 在最后一个Activity.onDestory()调用之后被调用 <br>
	 * 按Home键返回或被其他应用覆盖不会触发<br>
	 * 只有一直按Back退出才会触发
	 */
	public void onApplicationStop() {
		Log.i("application", "onApplicationStop");
		sessionId = null;
	}

	/**
	 * Application从后台唤醒到前台时调用
	 * <p>
	 * onApplicationStart<br>
	 * |-onApplicationResume<br>
	 * |-onApplicationPause<br>
	 * onApplicationStop
	 */
	public void onApplicationResume() {
		Log.i("application", "onApplicationResume");
	}

	/**
	 * Application从前台置为后台时调用，比如按Home键
	 * <p>
	 * onApplicationStart<br>
	 * |-onApplicationResume<br>
	 * |-onApplicationPause<br>
	 * onApplicationStop
	 */
	public void onApplicationPause() {
		Log.i("application", "onApplicationPause");
	}



	String sessionId() {
		return sessionId;
	}

	//
	// Application lifecycle management
	//
	private static int liveCounter;
	private static int activeCounter;
	private static Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if ((--liveCounter) == 0) {
					DSApplication.instance().onApplicationStop();
				}
			}
			if (msg.what == 2) {
				// skip a event loop in case onPause or onDestory takes too long
				sendEmptyMessageDelayed(3, 100);
			}
			if (msg.what == 3) {
				if ((--activeCounter) == 0) {
					DSApplication.instance().onApplicationPause();
				}
			}
		}
	};

	public void activityOnCreate(Activity a) {
		if (liveCounter++ == 0) {
			onApplicationStart();
		}
	}

	public void activityOnDestory(Activity a) {
		handler.sendEmptyMessage(1);
	}

	public void activityOnResume(Activity a) {
		if (activeCounter++ == 0) {
			DSApplication.instance().onApplicationResume();
		}
	}

	public void activityOnPause(Activity a) {
		handler.sendEmptyMessage(2);
	}
}
