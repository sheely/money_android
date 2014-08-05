package com.xdamon.download;

import java.io.File;
import java.util.HashMap;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import com.xdamon.download.impl.DefaultDownloadListener;
import com.xdamon.download.impl.DefaultDownloadTask;
import com.xdamon.download.intf.DownloadTask;

public class DefaultDownloadService extends Service {

	public static final String ACTION_DOWNLOAD_START = "com.efte.download.START";
	public static final String ACTION_DOWNLOAD_STOP = "com.efte.download.STOP";
	public static final String ACTION_DOWNLOAD_RESUME = "com.efte.download.RESUME";
	public static final String ACTION_DOWNLOAD_PAUSE = "com.efte.download.PAUSE";
	public static final String ACTION_SERVICE_STOP = "com.efte.download.service.STOP";

	private HashMap<String, DownloadTask> taskHashMap = new HashMap<String, DownloadTask>();

	public static void serviceStart(Context context, String downloadUrl) {
		serviceStart(context, downloadUrl, context.getFilesDir());
	}

	public static void serviceStart(Context context, String downloadUrl, File saveDir) {
		Intent intent = new Intent(context, DefaultDownloadService.class);
		intent.setAction(ACTION_DOWNLOAD_START);
		intent.putExtra("url", downloadUrl);
		intent.putExtra("savedir", saveDir.getPath());
		context.startService(intent);
	}

	public static void serviceStop(Context context, String downloadUrl) {
		Intent intent = new Intent(context, DefaultDownloadService.class);
		intent.setAction(ACTION_DOWNLOAD_STOP);
		intent.putExtra("url", downloadUrl);
		context.startService(intent);
	}

	public static void servicePause(Context context, String downloadUrl) {
		Intent intent = new Intent(context, DefaultDownloadService.class);
		intent.setAction(ACTION_DOWNLOAD_PAUSE);
		intent.putExtra("url", downloadUrl);
		context.startService(intent);
	}

	public static void serviceResume(Context context, String downloadUrl) {
		Intent intent = new Intent(context, DefaultDownloadService.class);
		intent.setAction(ACTION_DOWNLOAD_RESUME);
		intent.putExtra("url", downloadUrl);
		context.startService(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String url = intent.getStringExtra("url");
		if (TextUtils.isEmpty(url)) {
			throw new IllegalArgumentException("the url is illegal");
		}
		if (ACTION_DOWNLOAD_START.equals(intent.getAction())) {
			File saveDir = new File(intent.getStringExtra("savedir"));
			start(url, saveDir);
		} else if (ACTION_DOWNLOAD_STOP.equals(intent.getAction())) {
			stop(url);
		} else if (ACTION_DOWNLOAD_PAUSE.equals(intent.getAction())) {
			pause(url);
		} else if (ACTION_DOWNLOAD_RESUME.equals(intent.getAction())) {
			resume(url);
		} else if (ACTION_SERVICE_STOP.equals(intent.getAction())) {
			stopAll();
		}
		return START_NOT_STICKY;
	}

	private void start(String url, File saveDir) {
		DownloadTask oldTask = findTask(url);
		if (oldTask != null) {
			oldTask.stop(true);
		}
		DownloadTask task = new DefaultDownloadTask(this, saveDir, new DefaultDownloadListener(this));
		task.start(url);
		taskHashMap.put(url, task);
	}

	private void pause(String url) {
		DownloadTask oldTask = findTask(url);
		if (oldTask != null) {
			oldTask.pause();
		}
	}

	private void resume(String url) {
		DownloadTask oldTask = findTask(url);
		if (oldTask != null) {
			oldTask.resume();
		}
	}

	private void stop(String url) {
		if (!TextUtils.isEmpty(url) && taskHashMap.containsKey(url)) {
			taskHashMap.get(url).stop(true);
			taskHashMap.remove(url);
		}
		if (taskHashMap.size() == 0) {
			stopSelf();
		}
	}

	private void stopAll() {
		for (DownloadTask task : taskHashMap.values()) {
			task.stop(true);
		}
		taskHashMap.clear();
		stopSelf();
	}

	private DownloadTask findTask(String url) {
		for (String str : taskHashMap.keySet()) {
			if (url.equals(str)) {
				return taskHashMap.get(str);
			}
		}
		return null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new DownloadBinder();
	}

	public class DownloadBinder extends Binder {

		public DefaultDownloadService getService() {
			return DefaultDownloadService.this;
		}

	}
}
