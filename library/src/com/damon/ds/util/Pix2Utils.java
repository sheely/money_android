package com.damon.ds.util;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;

public class Pix2Utils {

	/**
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		if (context == null) {
			return (int) dipValue;
		}
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		if (context == null) {
			return (int) pxValue;
		}
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * @param context
	 * @param px
	 * @return
	 */
	public static float px2sp(Context context, Float px) {
		if (context == null) {
			return px.intValue();
		}
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return px / scaledDensity;
	}

	/**
	 * @param context
	 * @param sp
	 * @return
	 */
	public static float sp2px(Context context, float sp) {
		if (context == null) {
			return sp;
		}
		Resources r = context.getResources();
		float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, r.getDisplayMetrics());
		return size;
	}

}
