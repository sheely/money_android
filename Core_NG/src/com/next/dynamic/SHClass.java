package com.next.dynamic;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.SharedPreferences;
import com.next.app.StandardApplication;
import com.next.util.SHEnvironment;

public class SHClass {

	private static SHClassLoader mLoader;
	private static JSONObject mReplace;
	private static Boolean mIsLoad = false;

	
	private static synchronized void init() {
		if (mIsLoad != true) {
			SharedPreferences sp = StandardApplication.getInstance()
					.getSharedPreferences("update", Context.MODE_PRIVATE);
		//	if (sp.contains(SHEnvironment.getInstance().getVersion().toString())) {
				String path = StandardApplication.getInstance().getFilesDir()
						.getAbsolutePath();
				mLoader = new SHClassLoader(path + "/__u.jar", path, null,
						StandardApplication.getInstance().getClassLoader());
				File file = new File(path + "/__r.bin");
				FileInputStream fis;
				try {

					fis = new FileInputStream(file);
					byte[] bytes = new byte[fis.available()];
					fis.read(bytes);
					mReplace = new JSONObject(new String(bytes));
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
				mIsLoad = true;
			}
		//}
	}

	
	public static synchronized Class<?> getClass(String name) {
		SHClass.init();
		String className = SHClass.getClassName(name);
		Class<?> clazz = null;
		try {
			if (mLoader != null) {
				clazz = mLoader.loadClass(className);
			} else {
				clazz = StandardApplication.getInstance().getClassLoader()
						.loadClass(className);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clazz;
	}

	public static synchronized String getClassName(String cl) {
		SHClass.init();
		String name = null;
		if (mReplace != null && mReplace.has(cl)) {
			try {
				name = mReplace.getString(cl);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			name = cl;
		}
		return name;
	}

	public static synchronized void reFresh() {
		SHClass.init();
	}

	public static synchronized <T> T getInstance(String cl) {
		SHClass.init();
	
		Class<?> clazz = null;
		clazz = SHClass.getClass(cl);
		try {
			return (T) clazz.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static synchronized <T> T getInstance(Class<T> cl) {
		return SHClass.getInstance(cl.getName());
	}
}
