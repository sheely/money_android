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

}
