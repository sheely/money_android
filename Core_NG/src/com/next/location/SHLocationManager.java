package com.next.location;

import android.content.Intent;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.next.app.StandardApplication;

public class SHLocationManager {

	private static SHLocationManager __instance;
	private String mType = TYPE_BAIDU;
	public static String TYPE_BAIDU = "baidu";

	/**
	 * 变化
	 */
	public static String CORE_NOTIFICATION_LOCATION_LOCATION_CHANGED = "core_notification_location_changed";
	/**
	 * 成功
	 */
	public static String CORE_NOTIFICATION_LOCATION_LOCATION_SUCCESSFUL = "core_notification_location_successful";
	/**
	 * location
	 */
	private LocationClient mLocationClient = null;
	private SHLocation mLocation;

	public SHLocation getLocation() {
		return mLocation;
	}

	public SHLocationManager() {

	}

	public static SHLocationManager getInstance() {
		if (__instance == null) {
			__instance = new SHLocationManager();
		}
		return __instance;
	}

	/**
	 * 请求location
	 */
	public void requestLocation()	{
		mLocationClient.requestLocation();
	}
	/**
	 * 请求开始
	 */
	
	public void cancel(){
		mLocationClient.stop();
	}
	/**
	 * 开始
	 */
	public void start() {
		if (mType.equals(TYPE_BAIDU)) {
			mLocationClient = new LocationClient(StandardApplication.getInstance());
			//mLocationClient.setAccessKey("Kj9GRjLQAdhVDXPIfAxEajAG");
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);
			option.setCoorType("bd09ll");
			option.setScanSpan(600000);
			option.setServiceName("com.baidu.location.service_v2.9");
			option.setPoiExtraInfo(true);
			option.setPriority(LocationClientOption.NetWorkFirst);
			option.setAddrType("all");
			option.disableCache(true);
			mLocationClient.registerLocationListener(new BaiduLocationListenner());
			mLocationClient.setLocOption(option);
			mLocationClient.start();
			//mLocationClient.requestLocation();
		}
	}

	public void stop() {
		mLocationClient.stop();
	}

	public class BaiduLocationListenner implements
			com.baidu.location.BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation arg0) {
			// TODO Auto-generated method stub
			if (arg0.getLatitude() != Double.MIN_VALUE) {
				SHLocation location = new SHLocation(arg0.getLongitude(), arg0.getLatitude());
				location.setCity(arg0.getCity());
				location.setPrivince(arg0.getProvince());
				location.setStreet(arg0.getStreet());
				location.setAddress(arg0.getAddrStr());
				location.setDistricts(arg0.getDistrict());
				if (mLocation == null) {
					mLocation = location;
					Intent intent = new Intent(
							CORE_NOTIFICATION_LOCATION_LOCATION_SUCCESSFUL);
					StandardApplication.getInstance().sendBroadcast(intent);
				}
				mLocation = location;
				Intent intent = new Intent(
						CORE_NOTIFICATION_LOCATION_LOCATION_CHANGED);
				StandardApplication.getInstance().sendBroadcast(intent);
			}
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub

		}

	}
}
