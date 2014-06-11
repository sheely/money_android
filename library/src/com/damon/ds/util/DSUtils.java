package com.damon.ds.util;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;

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

}
