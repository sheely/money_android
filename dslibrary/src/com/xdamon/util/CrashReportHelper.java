package com.xdamon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Process;

public class CrashReportHelper {
	private static final String TAG = CrashReportHelper.class.getSimpleName();
	private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

	public static int versionCode = 0;
	public static String versionName = null;
	public static File reportFile;
	public static long lastOutOfMemoryMills;
	public static boolean debug = true;
	private static Context mContext;

	public static java.util.LinkedList<String> listSchema = new java.util.LinkedList<String>();

	// 记录URL Schema 跳转历史，打印到Crash Log中，方便定位错误位置

	public static void initialize(Context context) {
		if (reportFile != null)
			return;
		mContext = context;
		PackageInfo pi = AndroidUtils.packageInfo(context);
		versionCode = AndroidUtils.versionCode(context);
		versionName = AndroidUtils.versionName(context);
		if (pi.applicationInfo != null) {
			debug = (pi.applicationInfo.flags & 0x2) != 0;
			// ApplicationInfo.FLAG_DEBUGGABLE
		}

		reportFile = new File(context.getFilesDir(), "crash_report");
		File oomFile = new File(reportFile.getParent(), "out_of_memory");
		if (oomFile.exists()) {
			lastOutOfMemoryMills = oomFile.lastModified();
			oomFile.delete();
		}
	}

	public static void installSafeLooper() {
		SafeLooper.install();

		// 不抛出去
		defaultHandler = null;
		SafeLooper.setUncaughtExceptionHandler(unknownCrashHandler);
	}

	/**
	 * 向栈中添加一条新的URL Schema。
	 * 
	 * @param urlSchema
	 */
	public static void putUrlSchema(String urlSchema) {
		if (urlSchema == null || urlSchema.length() == 0) {
			return;
		}
		DSLog.d(TAG, "urlSchema: " + urlSchema);
		if (listSchema.size() > 10) {
			listSchema.removeLast();
		}
		listSchema.addFirst(urlSchema);
	}

	private static boolean hasOutOfMemoryError(Throwable ex) {
		if (ex == null)
			return false;
		Throwable next = ex.getCause();
		for (int i = 0; i < 0xF; i++) {
			if (ex instanceof OutOfMemoryError)
				return true;
			if (next == null || next == ex)
				return false;
			ex = next;
			next = ex.getCause();
		}
		return false;
	}

	private static Thread.UncaughtExceptionHandler defaultHandler;
	private static final CrashHandler unknownCrashHandler;

	static {
		defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		unknownCrashHandler = new CrashHandler();
		Thread.setDefaultUncaughtExceptionHandler(unknownCrashHandler);
	}

	public static class CrashHandler implements Thread.UncaughtExceptionHandler {
		@Override
		public final void uncaughtException(Thread thread, Throwable ex) {
			boolean oom = false;
			try {
				if (reportFile == null)
					return;
				if (oom = hasOutOfMemoryError(ex)) {
					File oomFile = new File(reportFile.getParent(), "out_of_memory");
					oomFile.delete();
					oomFile.createNewFile();
				}
				PrintWriter w = new PrintWriter(reportFile, "utf-8");

				w.print("===============================");
				w.print(UUID.randomUUID().toString());
				w.println("============================");

				if (debug)
					w.println("debug=true");

				w.print("addtime=");

				w.println(fmt.format(Calendar.getInstance()));

				w.print("user-agent=");
				w.println("android");

				w.print("deviceid=");
				w.println(AndroidUtils.imei());

				w.print("sessionid=");
				w.println("");

				w.print("network=");

				w.println(NetworkUtils.getNetworkInfo(mContext));

				w.print("os-version=");
				w.println(Build.VERSION.RELEASE);

				w.print("os-build=");
				w.println(Build.ID);

				w.print("device-brand=");
				w.println(Build.BRAND);

				w.print("device-model=");
				w.println(Build.MODEL);

				w.print("device-fingerprint=");
				w.println(Build.FINGERPRINT);

				w.print("thread=");
				w.println(thread.getName());

				w.println();
				ex.printStackTrace(w);
				w.println();
				w.println();

				w.println("Url Schema history:");
				for (String url : listSchema) {
					w.println(url);
				}

				w.close();
			} catch (Throwable t) {
			} finally {
				if (oom) {
					Process.killProcess(Process.myPid());
				} else if (defaultHandler != null) {
					defaultHandler.uncaughtException(thread, ex);
				}
			}
		}
	}

	public static boolean isAvailable() {
		if (reportFile == null)
			return false;
		if (!reportFile.exists())
			return false;
		return true;
	}

	public static String getReport() {
		if (reportFile == null)
			return null;
		try {
			FileInputStream fis = new FileInputStream(reportFile);
			if (fis.available() > 64 * 1000) {
				fis.close();
				return null;
			}
			byte[] buf = new byte[fis.available()];
			fis.read(buf);
			fis.close();
			String str = new String(buf);
			return str;
		} catch (IOException e) {
			return null;
		}
	}

	public static String getReportBak() {
		if (reportFile == null)
			return null;
		File bak = new File(reportFile.getParent(), reportFile.getName() + ".bak");
		try {
			FileInputStream fis = new FileInputStream(bak);
			if (fis.available() > 64 * 1000) {
				fis.close();
				return null;
			}
			byte[] buf = new byte[fis.available()];
			fis.read(buf);
			fis.close();
			String str = new String(buf);
			return str;
		} catch (IOException e) {
			return null;
		}
	}

	public static boolean deleteReport() {
		if (reportFile == null)
			return false;
		File bak = new File(reportFile.getParent(), reportFile.getName() + ".bak");
		bak.delete();
		return reportFile.renameTo(bak);
	}

	public static void sendAndDelete() {
		if (!isAvailable())
			return;
		final String report = getReport();
		deleteReport();
		DSLog.i(TAG, report);
		if (report == null || !report.startsWith("====") || report.contains("debug=true"))
			return;
		new Thread("Send Crash Report") {
			@Override
			public void run() {
				// send report to server
			}
		}.start();
	}
}
