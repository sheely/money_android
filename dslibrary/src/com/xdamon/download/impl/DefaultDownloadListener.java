package com.xdamon.download.impl;

import java.io.File;
import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.xdamon.library.R;
import com.xdamon.download.DefaultDownloadService;
import com.xdamon.download.intf.DownloadListener;
import com.xdamon.download.intf.DownloadTask;

public class DefaultDownloadListener implements DownloadListener {

	private static final int RESULT_RUNNING = 1;
	private static final int RESULT_FAILED = 2;
	private static final int RESULT_CANCEL = 3;

	private Context mContext;
	private NotificationManager notificationManager;
	private RemoteViews remoteViews;
	private String downloadUrl;

	public DefaultDownloadListener(Context context) {
		mContext = context;
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		remoteViews = new RemoteViews(context.getPackageName(), R.layout.notify);
	}

	@Override
	public void onStart(DownloadTask task) {
		downloadUrl = task.downloadUrl();
		showNotification(0, RESULT_RUNNING);
	}

	@Override
	public void onDoing(DownloadTask task, int progress) {
		downloadUrl = task.downloadUrl();
		showNotification(progress, RESULT_RUNNING);
	}

	@Override
	public void onPause(DownloadTask task, String message) {

	}

	@Override
	public void onResume(DownloadTask task) {

	}

	@Override
	public void onSuccess(DownloadTask task) {
		downloadUrl = task.downloadUrl();
		showNotification(100, RESULT_RUNNING);
		gotoInstall(task.downloadFile());
	}

	@Override
	public void onFailed(DownloadTask task, String message) {
		downloadUrl = task.downloadUrl();
		showNotification(0, RESULT_FAILED);
	}

	@Override
	public void onCancel(DownloadTask task) {
		downloadUrl = task.downloadUrl();
		showNotification(0, RESULT_CANCEL);
	}

	public void showNotification(int percent, int status) {
		Notification notification = new NotificationCompat.Builder(mContext).setWhen(System.currentTimeMillis()).build();
		if (status == RESULT_RUNNING) {
			if (percent == 0) {
				notification.tickerText = "准备下载";
				notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
				notification.contentView = remoteViews;
				notification.contentView.setProgressBar(R.id.update_notification_progressbar, 100, percent, true);
				notification.contentView.setTextViewText(R.id.update_notification_progresstext, percent + "%");
				notification.contentView.setTextViewText(R.id.update_notification_title, "正在准备下载（点击取消）");

				Intent notificationIntent = new Intent(mContext, DefaultDownloadService.class);
				notificationIntent.setAction(DefaultDownloadService.ACTION_DOWNLOAD_STOP);
				notificationIntent.putExtra("url", downloadUrl);
				PendingIntent contentIntent = PendingIntent.getService(mContext, downloadUrl.hashCode(),
					notificationIntent, 0);
				notification.contentIntent = contentIntent;
			} else if (percent == 100) {
				notification.tickerText = "完成";
				notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
				notification.contentView = remoteViews;
				notification.contentView.setProgressBar(R.id.update_notification_progressbar, 100, percent, false);
				notification.contentView.setTextViewText(R.id.update_notification_progresstext, percent + "%");
				notification.contentView.setTextViewText(R.id.update_notification_title, "下载完成");
			} else {
				notification.tickerText = "下载中";
				notification.contentView = remoteViews;
				notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
				notification.contentView.setProgressBar(R.id.update_notification_progressbar, 100, percent, false);
				notification.contentView.setTextViewText(R.id.update_notification_progresstext, percent + "%");
				notification.contentView.setTextViewText(R.id.update_notification_title, "正在下载（点击取消）");

				Intent notificationIntent = new Intent(mContext, DefaultDownloadService.class);
				notificationIntent.setAction(DefaultDownloadService.ACTION_DOWNLOAD_STOP);
				notificationIntent.putExtra("url", downloadUrl);
				PendingIntent contentIntent = PendingIntent.getService(mContext, downloadUrl.hashCode(),
					notificationIntent, 0);
				notification.contentIntent = contentIntent;
			}
		} else if (status == RESULT_CANCEL) {
			notification.tickerText = "已取消";
			notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
			notification.contentView = remoteViews;
			notification.contentView.setViewVisibility(R.id.update_notification_progressbar, View.GONE);
			notification.contentView.setTextViewText(R.id.update_notification_title, "已取消下载");
		} else if (status == RESULT_FAILED) {
			notification.tickerText = "文件下载失败！";
			notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
			notification.contentView = remoteViews;
			notification.contentView.setViewVisibility(R.id.update_notification_progressbar, View.GONE);
			notification.contentView.setTextViewText(R.id.update_notification_title, "验证失败，请重新下载");
		}

		notificationManager.notify(downloadUrl.hashCode(), notification);
	}

	public void gotoInstall(File file) {
		checkmod("777", file.getAbsolutePath());

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = "application/vnd.android.package-archive";
		intent.setDataAndType(Uri.fromFile(file), type);
		mContext.startActivity(intent);
	}

	public void checkmod(String permission, String path) {
		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
		}
	}

}
