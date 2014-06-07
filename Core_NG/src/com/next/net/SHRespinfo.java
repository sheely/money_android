package com.next.net;

import android.app.AlertDialog.Builder;
import android.content.Context;

public class SHRespinfo {
	
	protected int mResultCode = 0;
	void setCode(int value){
		mResultCode = value;
	}
	public int getCode(){
		return mResultCode;
	}
	protected String mMessage = "";
	void setMessage(String msg){
		mMessage = msg;
	}
	public String getMessage(){
		return mMessage;
	}
	/**
	 * show error
	 */
	public void show(Context x){
		Builder builder = new Builder(x);
		builder.setTitle("提示(" + this.mResultCode + ")");
		builder.setMessage(this.mMessage);
		builder.setPositiveButton("确定", null);
		builder.show();
	}
}
