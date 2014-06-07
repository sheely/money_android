package com.next.location;

public class SHLocation {
	double lng;// 精度
	double lat;// 纬度

	public double getLatitude() {
		return lat;
	}

	public double getLongitude() {
		return lng;
	}

	private String mCity = "";
	private String mPrivince = "";
	private String mStreet = "";
	private String mAddress = "";
	private String mDistrict ="";
	public void setDistricts(String name){
		mDistrict = name;
	}
	public String getDistrict(){
		return mDistrict;
	}
	public void setAddress(String name){
		mAddress = name;
	}
	public String getAddress(){
		return mAddress;
	}
	public void setCity(String name){
		mCity = name;
	}
	public void setPrivince(String name){
		mPrivince = name;
	}
	public void setStreet(String name){
		mStreet = name;
	}
	/**
	 * 
	 * @param name
	 * @return
	 */
	public String getCity(){
		return mCity;
	}
	public String getPrivince(){
		return mPrivince;
	}
	public String getStreet(){
		return mStreet ;
	}
	public SHLocation(double lng, double lat) {
		this.lng = lng;
		this.lat = lat;
	}

}
