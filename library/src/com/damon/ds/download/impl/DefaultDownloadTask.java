package com.damon.ds.download.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Pair;
import com.damon.ds.download.intf.DownloadListener;
import com.damon.ds.download.intf.DownloadTask;

public class DefaultDownloadTask extends AsyncTask<String, Integer, Pair<Integer, String>> implements DownloadTask {

	protected static final int IO_TRY_COUNT = 10;
	protected static final int RESULT_SUCCESS = 1;
	protected static final int RESULT_FAILED = 2;
	protected static final int RESULT_CANCEL = 3;

	protected Context mContext;
	protected DownloadListener downloadListener;

	protected String downloadUrl;
	protected boolean isPause;
	protected File fileDir;
	protected String fileName;
	protected boolean isFinish;
	protected Object block = new Object();

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			if (info != null && info.isConnected()) {
				resume();
			}
		};
	};

	public DefaultDownloadTask(Context context) {
		this(context, context.getCacheDir(), null);
	}

	public DefaultDownloadTask(Context context, File saveDir) {
		this(context, saveDir, null);
	}

	public DefaultDownloadTask(Context context, File saveDir, DownloadListener listener) {
		mContext = context;
		fileDir = saveDir;
		downloadListener = listener;
	}

	private void onPause(String message) {
		if (downloadListener != null) {
			downloadListener.onPause(this, message);
		}
	}

	@Override
	public void start(String url) {
		if (TextUtils.isEmpty(url)) {
			throw new IllegalArgumentException("the url is illegal");
		}
		downloadUrl = url;
		fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
		execute(url);
	}

	@Override
	public String downloadUrl() {
		return downloadUrl;
	}

	@Override
	public String fileName() {
		return fileName;
	}

	@Override
	public File downloadFile() {
		return new File(fileDir, fileName);
	}

	@Override
	public void pause() {
		isPause = true;
	}

	@Override
	public void resume() {
		if (downloadListener != null) {
			downloadListener.onResume(this);
		}
		isPause = false;
		synchronized (block) {
			block.notify();
		}
	}

	@Override
	public void stop(boolean mayInterruptIfRunning) {
		cancel(mayInterruptIfRunning);
	}

	private File incomingFile() {
		return new File(fileDir, fileName + "-incoming");
	}

	private long downloadedSize() {
		File file = downloadFile();
		if (file.exists()) {
			return file.length();
		}
		return 0;
	}

	private boolean checkDirs() {
		if (!fileDir.exists()) {
			return fileDir.mkdirs();
		}
		return true;
	}

	@SuppressLint("DefaultLocale")
	public boolean isFileOk() {
		if (fileName.toLowerCase().endsWith(".apk")) {
			return unzip(incomingFile());
		}
		return true;
	}

	/**
	 * 服务器是否支持断点续传
	 * 
	 * @return
	 */
	public boolean isSupportBreakpoint() {
		return false;
	}

	private boolean isConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info == null || !info.isConnected()) {
			return false;
		}
		return true;
	}

	protected boolean unzip(File unZipFile) {
		boolean succeed = true;
		ZipInputStream zin = null;
		ZipEntry entry = null;
		try {
			zin = new ZipInputStream(new FileInputStream(unZipFile));
			boolean first = true;
			while (true) {
				if ((entry = zin.getNextEntry()) == null) {
					if (first)
						succeed = false;
					break;
				}
				first = false;
				if (entry.isDirectory()) {
					zin.closeEntry();
					continue;
				}
				if (!entry.isDirectory()) {
					byte[] b = new byte[1024];
					@SuppressWarnings("unused")
					int len = 0;
					while ((len = zin.read(b)) != -1) {
					}
					zin.closeEntry();
				}
			}
		} catch (IOException e) {
			succeed = false;
		} finally {
			if (null != zin) {
				try {
					zin.close();
				} catch (IOException e) {
				}
			}
		}
		return succeed;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		int progress = values[0];
		if (downloadListener != null) {
			downloadListener.onDoing(this, progress);
		}
	}

	@Override
	protected void onPreExecute() {
		if (downloadListener != null) {
			downloadListener.onStart(this);
		}
		mContext.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}

	@Override
	protected void onPostExecute(Pair<Integer, String> result) {
		int resultCode = result.first;
		boolean success = incomingFile().renameTo(downloadFile());
		if (downloadListener != null) {
			if (resultCode == RESULT_SUCCESS) {
				if (success) {
					downloadListener.onSuccess(this);
				} else {
					downloadListener.onFailed(this, "文件重命名失败");
				}
			} else if (resultCode == RESULT_FAILED) {
				downloadListener.onFailed(this, result.second);
			}
		}
	}
	
	@Override
	protected void onCancelled(Pair<Integer, String> result) {
		if (downloadListener != null) {
			downloadListener.onCancel(this);
		}
		unregister();
	}
	
	private void unregister() {
		try {
			mContext.unregisterReceiver(receiver);
		} catch (Exception e) {

		}
	}

	@Override
	protected Pair<Integer, String> doInBackground(String... params) {
		downloadUrl = params[0];
		if (!checkDirs()) {
			return new Pair<Integer, String>(RESULT_FAILED, "文件初始化失败");
		}
		int io_try = 0;
		while (!isFinish) {
			if (isCancelled()) {
				break;
			}
			synchronized (block) {
				try {
					if (!isConnected()) {
						onPause("请检查网络链接");
						block.wait();
					} else if (isPause) {
						onPause("暂停下载");
						block.wait();
					}
				} catch (InterruptedException e) {

				}
			}
			double fileSize = 0;
			byte[] buffer = new byte[4096];
			int readLength = 0;
			int lastPercent = 0;
			int percent = 0;
			long downloadSize = 0;

			URL url = null;
			HttpURLConnection urlConnection = null;
			FileOutputStream fos = null;
			try {
				url = new URL(downloadUrl);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setConnectTimeout(15000);
				urlConnection.setReadTimeout(15000);

				boolean isBreakpointResume = false;
				if (isSupportBreakpoint() && downloadedSize() > 0) {
					urlConnection.setRequestProperty("Range", "bytes=" + downloadedSize());
					downloadSize += downloadedSize();
					fileSize = downloadSize;
					isBreakpointResume = true;
				}
				urlConnection.connect();
				fileSize += urlConnection.getContentLength();

				fos = new FileOutputStream(incomingFile(), isBreakpointResume);

				InputStream inputStream = urlConnection.getInputStream();
				while ((readLength = inputStream.read(buffer)) > 0) {
					if (isCancelled()) {
						break;
					}
					fos.write(buffer, 0, readLength);
					downloadSize += readLength;
					percent = (int) ((downloadSize / fileSize) * 100);
					if (percent - lastPercent >= 1) {
						publishProgress(percent);
						lastPercent = percent;
					}
				}
				if(!isCancelled()){
					isFinish = true;
				}
			} catch (MalformedURLException e) {
				return new Pair<Integer, String>(RESULT_FAILED, "下载链接错误");
			} catch (IOException e) {
				if (++io_try > IO_TRY_COUNT) {
					return new Pair<Integer, String>(RESULT_FAILED, "下载文件失败");
				} else {
					try {
						Thread.sleep(5000);
						continue;
					} catch (InterruptedException ex) {

					}
				}
			} catch (Exception e) {
				return new Pair<Integer, String>(RESULT_FAILED, "未知错误");
			} finally {
				unregister();
				try {
					if (fos != null) {
						fos.close();
					}
					if (urlConnection != null) {
						urlConnection.disconnect();
					}
				} catch (IOException e) {
					return new Pair<Integer, String>(RESULT_FAILED, "未知错误");
				}
			}
		}
		if (isFinish) {
			if (!isFileOk()) {
				return new Pair<Integer, String>(RESULT_FAILED, "文件校验失败");
			}
		}
		return new Pair<Integer, String>(RESULT_SUCCESS, "文件下载成功");
	}

}
