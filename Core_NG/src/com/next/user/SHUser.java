package com.next.user;

public class SHUser {

	private static final SHUser __instance = new SHUser();
	/**
	 * 单例
	 * @return
	 */
	public SHUser getInstance(){
		return __instance;
	}
	/**
	 * 用户id
	 */
	private String _userId;
	public String getUserID(){
		return _userId;
	}
	public void String(String value){
		_userId = value;
	}
}
