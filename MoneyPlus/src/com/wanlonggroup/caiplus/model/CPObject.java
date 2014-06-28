package com.wanlonggroup.caiplus.model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.next.util.Log;

public class CPObject implements Parcelable {
	
	public static final String TAG = "CPObject";
	
	protected JSONObject jsonObj;
	
	public CPObject(){
		jsonObj = new JSONObject();
	}
	
	public CPObject(JSONObject json){
		this.jsonObj = json;
	}
	
	public CPObject(String json){
		try {
			jsonObj = new JSONObject(json);
		} catch (Exception e) {
			jsonObj = new JSONObject();
			if(e != null){
				Log.e(TAG, e.getLocalizedMessage());
			}
		}
	}
	
	public static final Parcelable.Creator<CPObject> CREATOR = new Parcelable.Creator<CPObject>() {
		public CPObject createFromParcel(Parcel in) {
			return new CPObject(in);
		}

		public CPObject[] newArray(int size) {
			return new CPObject[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public void setData(JSONObject jsonObj){
		this.jsonObj = jsonObj;
	}
	
	public JSONObject getData(){
		return jsonObj;
	}
	
	public String getString(String name){
		return jsonObj.optString(name);
	}
	
	public JSONObject getJSONObject(String name){
		return jsonObj.optJSONObject(name);
	}
	
	public JSONArray getJSONArray(String name){
		return jsonObj.optJSONArray(name);
	}
	
	public CPObject(Parcel in) {
		String jsonStr = in.readString();
		try {
			jsonObj = new JSONObject(jsonStr);
		} catch (JSONException e) {
			jsonObj = new JSONObject();
			if(e != null){
				Log.e(TAG, e.getLocalizedMessage());
			}
		}
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		String jsonStr = new JSONObject().toString();
		if(jsonObj != null){
			jsonStr = jsonObj.toString();
		}
		dest.writeString(jsonStr);
	}
	
	public static CPObject fromObject(Object obj){
		if(obj instanceof JSONObject){
			return new CPObject((JSONObject) obj);
		}
		return new CPObject();
	}
	
	public static CPObject fromJson(String jsonStr){
		if(!TextUtils.isEmpty(jsonStr)){
			return new CPObject(jsonStr);
		}
		return new CPObject();
	}

}
