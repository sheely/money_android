package com.xdamon.app;

import java.io.File;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.next.app.StandardApplication;
import com.next.util.Log;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.xdamon.util.DSLog;

public abstract class DSApplication extends StandardApplication {

	private String sessionId;
	private static DSApplication _inner_instance;


	static DSApplication _instance() {
		return _inner_instance;
	}

	public DSApplication() {
	    _inner_instance = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		if ((getApplicationInfo().flags & 2) != 0) {
			// ApplicationInfo.FLAG_DEBUGGABLE
			DSLog.LEVEL = DSLog.VERBOSE;
			Log.LEVEL = Log.VERBOSE;
		} else {
			DSLog.LEVEL = Integer.MAX_VALUE;
			Log.LEVEL = Integer.MAX_VALUE;
		}

		initImageLoader(getApplicationContext());
	}

	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getCacheDirectory(context);
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator()).memoryCache(new LruMemoryCache(2 * 1024 * 1024))
		        .memoryCacheSize(2 * 1024 * 1024).discCache(new UnlimitedDiscCache(cacheDir)).discCacheSize(50 * 1024 * 1024)
		        .discCacheFileCount(100);
		ImageLoaderConfiguration config = builder.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
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
		DSLog.i("application", "onApplicationStart");

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
		DSLog.i("application", "onApplicationStop");
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
		DSLog.i("application", "onApplicationResume");
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
		DSLog.i("application", "onApplicationPause");
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
					DSApplication._instance().onApplicationStop();
				}
			}
			if (msg.what == 2) {
				// skip a event loop in case onPause or onDestory takes too long
				sendEmptyMessageDelayed(3, 100);
			}
			if (msg.what == 3) {
				if ((--activeCounter) == 0) {
					DSApplication._instance().onApplicationPause();
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
			DSApplication._instance().onApplicationResume();
		}
	}

	public void activityOnPause(Activity a) {
		handler.sendEmptyMessage(2);
	}
}
