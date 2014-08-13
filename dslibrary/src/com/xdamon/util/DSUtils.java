package com.xdamon.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.xdamon.app.DSObject;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;

public class DSUtils {
	
	
	public static int getScreenWidth(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	public static int getScreenHeight(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}
	
	public static int getStatusBarHeight(Activity activity){
		Rect rect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		return rect.top;
	}
	
	public static int getTitleBarHeight(Activity activity){
		int contentTop = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
		return contentTop - getStatusBarHeight(activity);
	}
	
	public static int getContentViewHeight(Activity activity){
		return  activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
	}
	
	/**
	 * Determines if given points are inside view
	 * 
	 * @param x
	 *            - x coordinate of point
	 * @param y
	 *            - y coordinate of point
	 * @param view
	 *            - view object to compare
	 * @return true if the points are within view bounds, false otherwise
	 */
	public static boolean isPointInsideView(float x, float y, View view) {
		int location[] = new int[2];
		view.getLocationOnScreen(location);
		int viewX = location[0];
		int viewY = location[1];

		// point is inside view bounds
		if ((x > viewX && x < (viewX + view.getWidth()))
				&& (y > viewY && y < (viewY + view.getHeight()))) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String parseImgPath(final Context mContext,Uri uri) {
		String path = null;
		if (uri != null) {
			if ("file".equals(uri.getScheme())) {
				String s = null;
				try {
					s = URLDecoder.decode(uri.toString().replaceFirst("file://", ""), "utf-8");
				} catch (UnsupportedEncodingException e) {
				}
				return s;
			}
			String[] proj = { MediaStore.Images.Media.DATA }; 
			Cursor cursor = mContext.getContentResolver().query(uri, proj, null, null, null);
			if (cursor == null)
				return null;
			try {
				cursor.moveToFirst();
				path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

			} catch (Exception e) {
				DSLog.e("parseImgPath", e.getLocalizedMessage());
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
		}
		return path;
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
