package com.damon.ds.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public class Base64ToBitmap {

	public static String getBitmapStrBase64(Bitmap bitmap, CompressFormat format) {
		String result = null;
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			bitmap.compress(format, 100, bos);
			byte[] bytes = bos.toByteArray();
			result = Base64.encodeBytes(bytes);
		} catch (Exception e) {

		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
			} catch (IOException exception) {

			}
		}
		return result;
	}

	public static Bitmap getBitmapFromBase64Str(String base64Str) {
		if (base64Str == null) {
			return null;
		}
		try {
			byte[] bytes = Base64.decode(base64Str);
			return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
