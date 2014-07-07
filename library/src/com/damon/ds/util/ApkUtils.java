package com.damon.ds.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.damon.ds.app.DSApplication;

public class ApkUtils {

	private static PackageInfo packageInfo;

	public static PackageInfo packageInfo() {
		if (packageInfo == null) {
			try {
				Context context = DSApplication.instance();
				packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return packageInfo;
	}

	public static String versionName() {
		return packageInfo().versionName;
	}

	public static int versionCode() {
		return packageInfo().versionCode;
	}

	private static String imei;

	/**
	 * 手机的IMEI设备序列号
	 * <p>
	 * 第一次启动时会保存该序列号，可以频繁调用
	 * 
	 * @return IMEI or "00000000000000" if error
	 */
	public static String imei() {
		if (imei == null) {
			// update cached imei when identity changed. including brand, model,
			// radio and system version
			String deviceIdentity = Build.VERSION.RELEASE + ";" + Build.MODEL + ";" + Build.BRAND;
			if (deviceIdentity.length() > 64) {
				deviceIdentity = deviceIdentity.substring(0, 64);
			}
			if (deviceIdentity.indexOf('\n') >= 0) {
				deviceIdentity = deviceIdentity.replace('\n', ' ');
			}

			String cachedIdentity = null;
			String cachedImei = null;
			try {
				// do not use file storage, use cached instead
				File path = new File(DSApplication.instance().getCacheDir(), "cached_imei");
				FileInputStream fis = new FileInputStream(path);
				byte[] buf = new byte[1024];
				int l = fis.read(buf);
				fis.close();
				String str = new String(buf, 0, l, "UTF-8");
				int a = str.indexOf('\n');
				cachedIdentity = str.substring(0, a);
				int b = str.indexOf('\n', a + 1);
				cachedImei = str.substring(a + 1, b);
			} catch (Exception e) {
			}

			if (deviceIdentity.equals(cachedIdentity)) {
				imei = cachedImei;
			} else {
				imei = null;
			}

			// cache fail, read from telephony manager
			if (imei == null) {
				try {
					TelephonyManager tel = (TelephonyManager) DSApplication.instance().getSystemService(
							Context.TELEPHONY_SERVICE);
					imei = tel.getDeviceId();
					if (imei != null) {
						if (imei.length() < 8) {
							imei = null;
						} else {
							char c0 = imei.charAt(0);
							boolean allSame = true;
							for (int i = 0, n = imei.length(); i < n; i++) {
								if (c0 != imei.charAt(i)) {
									allSame = false;
									break;
								}
							}
							if (allSame)
								imei = null;
						}
					}
				} catch (Exception e) {
				}
				if (imei != null) {
					try {
						File path = new File(DSApplication.instance().getCacheDir(), "cached_imei");
						FileOutputStream fos = new FileOutputStream(path);
						String str = deviceIdentity + "\n" + imei + "\n";
						fos.write(str.getBytes("UTF-8"));
						fos.close();
					} catch (Exception e) {
					}
				} else {
					File path = new File(DSApplication.instance().getCacheDir(), "cached_imei");
					path.delete();
				}
			}

			if (imei == null) {
				imei = "00000000000000";
			}
		}
		return imei;
	}
	
}
