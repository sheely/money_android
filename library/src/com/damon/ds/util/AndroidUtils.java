package com.damon.ds.util;

/*
 * Copyright (C) 2010 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

public class AndroidUtils {
	private AndroidUtils() {
	}

	public static String getAppName(Context context) {
		return getAppName(context, null);
	}

	public static String getAppName(Context context, String packageName) {
		String applicationName;

		if (packageName == null) {
			packageName = context.getPackageName();
		}

		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
			applicationName = context.getString(packageInfo.applicationInfo.labelRes);
		}
		catch (Exception e) {
			DSLog.w("Failed to get version number.", e.getLocalizedMessage());
			applicationName = "";
		}

		return applicationName;
	}

	public static String getAppVersionNumber(Context context) {
		return getAppVersionNumber(context, null);
	}

	public static String getAppVersionNumber(Context context, String packageName) {
		String versionName;

		if (packageName == null) {
			packageName = context.getPackageName();
		}

		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
			versionName = packageInfo.versionName;
		}
		catch (Exception e) {
			DSLog.w("Failed to get version number.", e.getLocalizedMessage());
			versionName = "";
		}

		return versionName;
	}

	public static String getAppVersionCode(Context context) {
		return getAppVersionCode(context, null);
	}

	public static String getAppVersionCode(Context context, String packageName) {
		String versionCode;

		if (packageName == null) {
			packageName = context.getPackageName();
		}

		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
			versionCode = Integer.toString(packageInfo.versionCode);
		}
		catch (Exception e) {
			DSLog.w("Failed to get version code.", e.getLocalizedMessage());
			versionCode = "";
		}

		return versionCode;
	}

	public static int getSdkVersion() {
		try {
			return Build.VERSION.class.getField("SDK_INT").getInt(null);
		}
		catch (Exception e) {
			return 3;
		}
	}

	public static boolean isEmulator() {
		return Build.MODEL.equals("sdk") || Build.MODEL.equals("google_sdk");
	}
	
	/**
	 * 版本号比较
	 * 
	 * @param a
	 * @param b
	 * @return if a = b return 0; a > b return 1;a < b return -1
	 */
	public static int compareVersion(String a, String b) {
		if (a.equals(b)) {
			return 0;
		}
		int[] target = versionNames(a);
		int[] current = versionNames(b);
		int len = Math.min(current.length, target.length);
		for (int i = 0; i < len; i++) {
			if (current[i] == target[i]) {
				continue;
			}
			return target[i] > current[i] ? 1 : -1;
		}
		if (target.length == current.length) {
			return 0;
		}
		return target.length > current.length ? 1 : -1;
	}

	private static int[] versionNames(String versionName) {
		if (TextUtils.isEmpty(versionName)) {
			return null;
		}
		String[] names = versionName.split("\\.");
		int[] intName = new int[names.length];
		for (int i = 0; i < names.length; i++) {
			intName[i] = Integer.valueOf(names[i].trim());
		}
		return intName;
	}
}