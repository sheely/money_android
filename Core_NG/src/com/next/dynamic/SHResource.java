package com.next.dynamic;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.next.app.StandardApplication;

import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;

public class SHResource {
	private static Class<?> mHook;

	public static void init() {
		if (mHook == null) {
			mHook = SHClass.getClass("com.sh.common.base.SHHook");
		}
	}

	public static Drawable getDrawable(String filename) {
		SHResource.init();
		InputStream is = mHook.getResourceAsStream("/res/drawable-xhdpi/"
				+ filename + ".png");
		// is = null;
		if (is != null) {
			return Drawable.createFromStream(is, "name");
		} else {

			int resID = StandardApplication
					.getInstance()
					.getResources()
					.getIdentifier(
							StandardApplication.getInstance().getPackInfo().packageName
									+ ":drawable/" + filename, null, null);
			return StandardApplication.getInstance().getResources()
					.getDrawable(resID);
		}
	}

	public static XmlPullParser getLayout(String filename) {
		SHResource.init();
		InputStream is = mHook.getResourceAsStream("/res/layout/" + filename
				+ ".xml");
		// is = null;
		if (is != null) {
			XmlPullParser parser = null;
			try {
				parser = XmlPullParserFactory.newInstance().newPullParser();
				parser.setInput(is, "utf-8");
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return parser;
		} else {

			int resID = StandardApplication
					.getInstance()
					.getResources()
					.getIdentifier(
							StandardApplication.getInstance().getPackInfo().packageName
									+ ":layout/" + filename, null, null);
			return StandardApplication.getInstance().getResources()
					.getLayout(resID);
		}
	}
}
