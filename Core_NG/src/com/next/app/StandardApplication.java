package com.next.app;

import com.next.util.Log;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class StandardApplication extends Application {

	private static StandardApplication _instance;
	private PackageInfo packInfo;

	public void onCreate() {
		super.onCreate();
		PackageManager pm = getPackageManager();
		try {
			packInfo = pm.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if ((getApplicationInfo().flags & 2) != 0) {
			Log.LEVEL = Log.VERBOSE;
		} else {
			Log.LEVEL = Integer.MAX_VALUE;
		}
		_instance = this;
	}

	public static StandardApplication getInstance() {
		return _instance;
	}
	
	public PackageInfo getPackInfo(){
		return packInfo;
	}
}
