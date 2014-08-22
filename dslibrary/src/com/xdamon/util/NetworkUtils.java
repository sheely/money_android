package com.xdamon.util;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

public class NetworkUtils {
	
	/**
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		if (context == null) {
			return false;
		}
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
		if (activeNetworkInfo == null) {
			return false;
		}
		if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnectNetwork(Context context) {
		boolean flag = false;
		try {
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo ni = manager.getActiveNetworkInfo();
			if (ni != null && ni.isConnected()) {
				flag = true;
			}
		} catch (Exception e) {
		}

		return flag;
	}
	
	public static boolean isWap(Context mContext) {
		ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
		if (activeNetInfo == null)
			return false;
		if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return false;
		}
		if (activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = activeNetInfo.getExtraInfo();
			if (extraInfo == null)
				return false;
			extraInfo = extraInfo.toLowerCase();
			if (extraInfo.contains("cmnet"))
				return false;
			if (extraInfo.contains("cmwap"))
				return true;
			if (extraInfo.contains("3gnet"))
				return false;
			if (extraInfo.contains("3gwap"))
				return true;
			if (extraInfo.contains("uninet"))
				return false;
			if (extraInfo.contains("uniwap"))
				return true;
			if (extraInfo.contains("ctnet"))
				return false;
			if (extraInfo.contains("ctwap"))
				return true;
			if (extraInfo.contains("#777")) {
				try {
					Cursor c = mContext.getContentResolver().query(Uri.parse("content://telephony/carriers/preferapn"),
							new String[] { "proxy", "port" }, null, null, null);
					if (c.moveToFirst()) {
						String host = c.getString(0);
						if (host.length() > 3) {
							return true;
						}
					}
					return false;
				} catch (Exception e) {
					return false;
				}
			}
		}
		return false;
	}
	
	/**
	 * 设备连接的网络类型，有以下几种返回：<br>
	 * wifi: 从无线网络连接<br>
	 * mobile***: 从手机网络连接。注意mobile后面会跟详细信息：<br>
	 * &nbsp;&nbsp;mobile(手机数据网络类型,连接点名称)<br>
	 * &nbsp;&nbsp;mobile(EDGE,cmnet)<br>
	 * &nbsp;&nbsp;mobile(UTMS,3gnet)<br>
	 * unknown: 未知网络类型<br>
	 * 其他...<br>
	 */
	public static String getNetworkInfo(Context context) {
		ConnectivityManager connectivityManager = connectivityManager(context);
		if (connectivityManager == null)
			return "unknown";
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo == null)
			return "unknown";
		switch (activeNetInfo.getType()) {
		case ConnectivityManager.TYPE_WIFI:
			return "wifi";
		case ConnectivityManager.TYPE_MOBILE:
			return "mobile(" + activeNetInfo.getSubtypeName() + ","
					+ activeNetInfo.getExtraInfo() + ")";
		default:
			return activeNetInfo.getTypeName();
		}
	}
	
	private static ConnectivityManager connectivityManager;

	public static ConnectivityManager connectivityManager(Context context) {
		if (connectivityManager == null) {
			try {
				connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			} catch (Exception e) {
				DSLog.w("network",
					"cannot get connectivity manager, maybe the permission is missing in AndroidManifest.xml?", e);
			}
		}
		return connectivityManager;
	}

}
