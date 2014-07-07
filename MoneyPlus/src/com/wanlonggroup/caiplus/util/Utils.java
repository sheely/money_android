package com.wanlonggroup.caiplus.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.damon.ds.app.DSObject;

public class Utils {

	public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);
	public static final DateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINESE);
	public static final DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINESE);
	public static final DateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
	public static final DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.CHINESE);

	public static String formate(String dateString) {
		try {

			Date d = dateFormat.parse(dateString.trim());
			return dateFormat.format(d);

		} catch (Exception e) {
		}
		return null;
	}

	public static boolean isDSObject(Object obj) {
		if (obj == null) {
			return false;
		}
		return obj instanceof DSObject;
	}

	public static boolean isDSObject(Object obj, String objName) {
		if (!isDSObject(obj)) {
			return false;
		}
		return ((DSObject) obj).isObject(objName);
	}
}
