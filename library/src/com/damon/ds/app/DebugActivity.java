package com.damon.ds.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;

import com.damon.ds.library.R;
import com.damon.ds.util.ApkUtils;
import com.damon.ds.util.DSLog;

public class DebugActivity extends DSActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onSetContentView();
		debugInfo();
	}
	
	public void onSetContentView(){
		setContentView(R.layout.debug_panel);
	}
	
	public void debugInfo() {
		try {
			PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
			StringBuilder s = new StringBuilder();
			s.append("versionName=").append(ApkUtils.versionName()).append('\n');
			s.append("versionCode=").append(ApkUtils.versionCode()).append('\n');
			s.append("debuggable=").append((pi.applicationInfo.flags & 0x2) != 0).append('\n');
			s.append("imei=").append(ApkUtils.imei()).append('\n');
			s.append("android.SDK=").append(Build.VERSION.SDK).append('\n');
			s.append("android.VERSION=").append(Build.VERSION.RELEASE).append('\n');
			s.append("android.ID=").append(Build.ID).append('\n');
			s.append("android.BRAND=").append(Build.BRAND).append('\n');
			s.append("android.MODEL=").append(Build.MODEL).append('\n');

			DisplayMetrics dm = new DisplayMetrics();
			WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			manager.getDefaultDisplay().getMetrics(dm);

			s.append('\n');
			s.append("widthPixels=" + dm.widthPixels).append('\n');
			s.append("heightPixels=" + dm.heightPixels).append('\n');
			s.append("density=" + dm.density).append('\n');
			s.append("densityDpi=" + dm.densityDpi).append('\n');
			s.append("scaledDensity=" + dm.scaledDensity).append('\n');
			s.append("xdpi=" + dm.xdpi).append('\n');
			s.append("ydpi=" + dm.ydpi).append('\n');
			s.append("DENSITY_LOW=" + DisplayMetrics.DENSITY_LOW).append('\n');
			s.append("DENSITY_MEDIUM=" + DisplayMetrics.DENSITY_MEDIUM).append('\n');
			s.append("DENSITY_TV=" + DisplayMetrics.DENSITY_TV).append('\n');
			s.append("DENSITY_HIGH=" + DisplayMetrics.DENSITY_HIGH).append('\n');
			s.append("DENSITY_XHIGH=" + DisplayMetrics.DENSITY_XHIGH).append('\n');

			s.append("dm=" + dm.toString()).append('\n');

			((TextView) findViewById(R.id.debug_info)).setText(s.toString());
		} catch (Exception e) {
			DSLog.e(e.getLocalizedMessage());
		}
	}
}
