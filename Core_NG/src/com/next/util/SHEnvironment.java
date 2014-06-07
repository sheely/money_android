package com.next.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.next.app.StandardApplication;
import com.next.config.SHVersion;

public class SHEnvironment {
	private final static SHEnvironment _instance =  new SHEnvironment();
	private String _loginId = "";
	private String _password = "";
	private SHVersion _version = null;
	private String _sessionid = "";
	private String _clientID ="";
	
	public void setLoginId(String value){
		_loginId = value;
	}
	public void setPassword(String value){
		_password = value;
	}
	public String getLoginID(){
		return _loginId;
	}
	public String getPassword(){
		return _password;
	}
	public void setClientID (String clientId ) {
		_clientID = clientId;
	}
	public String getClientID()
	{
		return _clientID;
	}
	public SHVersion getVersion()
	{
		if(_version == null){
			PackageManager manager = StandardApplication.getInstance().getPackageManager();
			PackageInfo info;
			try {
				info = manager.getPackageInfo(StandardApplication.getInstance().getPackageName(), 0);
				_version = new SHVersion(info.versionName);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				_version = new SHVersion("1.0.0");

			}
		}
		return _version;
	}
//	public void setVersion(String version){
//		_version = version;
//	}
	public String getSession(){
		return _sessionid;
	}
	public void setSession(String session){
		_sessionid = session;
	}
	public static  SHEnvironment getInstance(){
		return _instance;
	}
	//private static PackageInfo packageInfo;

	private static ConnectivityManager connectivityManager;

	private SHEnvironment() {
		_clientID = getIMEI(StandardApplication.getInstance());
	}
	public static String getIMEI(Context context) {
		if(context != null){
			TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(
					Context.TELEPHONY_SERVICE);
			return tm.getDeviceId();// 获取机器码
			}else{
				return "";
			}
	}

	private static ConnectivityManager connectivityManager() {
		if (connectivityManager == null) {
			try {
				Context context = StandardApplication.getInstance();
				connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			} catch (Exception e) {
				Log.w("network",
						"cannot get connectivity manager, maybe the permission is missing in AndroidManifest.xml?", e);
			}
		}
		return connectivityManager;
	}

	public static String getNetworkInfo() {
		ConnectivityManager connectivityManager = connectivityManager();
		if (connectivityManager == null)
			return "unknown";
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo == null)
			return "unknown";
		switch (activeNetInfo.getType()) {
		case ConnectivityManager.TYPE_WIFI:
			return "wifi";
		case ConnectivityManager.TYPE_MOBILE:
			return "mobile(" + activeNetInfo.getSubtypeName() + "," + activeNetInfo.getExtraInfo() + ")";
		default:
			return activeNetInfo.getTypeName();
		}
	}

	public static boolean hasNetwork() {
		Context context = StandardApplication.getInstance();
		ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mgr.getActiveNetworkInfo();
		if (info == null || !info.isConnected()) {
			return false;
		}
		return true;
	}

}
