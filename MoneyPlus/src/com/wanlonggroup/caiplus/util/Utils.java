package com.wanlonggroup.caiplus.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.text.TextUtils;

import com.xdamon.app.DSObject;

public class Utils {

	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	/**
	 * yyyy-MM-dd HH:mm
	 */
	public static final DateFormat dateTimeFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
	/**
	 * yyyy-MM-dd
	 */
	public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
	/**
	 * yyyy/MM/dd
	 */
	public static final DateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
	/**
	 * HH:mm
	 */
	public static final DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);

	public static String formate(String dateString) {
		return formate(dateFormat,dateString);
	}

	public static String formate(DateFormat format, String dateString) {
		try {
			return format.format(wrapDatetime(dateString));
		} catch (Exception e) {
		}
		return dateString;
	}

	public static Date wrapDatetime(String datetime) {
		try {
			if (!TextUtils.isEmpty(datetime)) {
				int space = datetime.trim().indexOf(" ");
				if (space > 0) {
					String date = datetime.split(" ")[0];
					String time = datetime.split(" ")[1];
					return dateTimeFormat.parse(wrapDate(date) + " " + wrapTime(time));
				} else if (datetime.indexOf("-") > 0) {
					return dateFormat.parse(wrapDate(datetime));
				} else {
					return timeFormat.parse(wrapDate(datetime));
				}
			}
		} catch (Exception e) {

		}
		return Calendar.getInstance().getTime();
	}

	public static String wrapDate(String date) {
		if (date.length() == 10) {
			return date;
		}
		StringBuffer buffer = new StringBuffer();
		String[] dates = date.split("-");
		for (String d : dates) {
			if (d.length() == 1) {
				buffer.append("0" + d);
			} else {
				buffer.append(d);
			}
			buffer.append("-");
		}
		return buffer.deleteCharAt(buffer.length() - 1).toString();
	}

	public static String wrapTime(String time) {
		if (time.length() == 8) {
			return time;
		}
		StringBuffer buffer = new StringBuffer();
		String[] times = time.split(":");
		for (String t : times) {
			if (t.length() == 1) {
				buffer.append("0" + t);
			} else {
				buffer.append(t);
			}
			buffer.append(":");
		}
		return buffer.deleteCharAt(buffer.length() - 1).toString();
	}

	public static String getCurrentTime(DateFormat format) {
		Calendar calendar = Calendar.getInstance();
		try {
			return format.format(calendar.getTime());
		} catch (Exception e) {

		}
		return "";
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
