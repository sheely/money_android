package com.damon.ds.app;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.next.util.Log;

public class DSObject implements Parcelable {

	public static final String TAG = "DSObject";

	protected JSONObject jsonObj;
	protected String objName;

	public DSObject() {
		jsonObj = new JSONObject();
	}

	public DSObject(String objName) {
		this();
		this.objName = objName;
	}

	public static final Parcelable.Creator<DSObject> CREATOR = new Parcelable.Creator<DSObject>() {
		public DSObject createFromParcel(Parcel in) {
			return new DSObject(in);
		}

		public DSObject[] newArray(int size) {
			return new DSObject[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	public boolean isObject(String objName) {
		if (TextUtils.isEmpty(this.objName) || TextUtils.isEmpty(objName)) {
			return false;
		}
		return this.objName.equals(objName);
	}

	public String getString(String name) {
		return jsonObj.optString(name);
	}
	
	public void put(String name,String value){
		try {
			this.jsonObj.put(name, value);
		} catch (JSONException e) {
			printException(e);
		}
	}
	
	public void put(String name,int value){
		try {
			this.jsonObj.put(name, value);
		} catch (JSONException e) {
			printException(e);
		}
	}
	
	public void put(String name,long value){
		try {
			this.jsonObj.put(name, value);
		} catch (JSONException e) {
			printException(e);
		}
	}
	
	public void put(String name,Object value){
		try {
			this.jsonObj.put(name, value);
		} catch (JSONException e) {
			printException(e);
		}
	}
	
	public void put(String name,double value){
		try {
			this.jsonObj.put(name, value);
		} catch (JSONException e) {
			printException(e);
		}
	}
	
	public void put(String name,boolean value){
		try {
			this.jsonObj.put(name, value);
		} catch (JSONException e) {
			printException(e);
		}
	}

	public JSONObject getJSONObject(String name) {
		return jsonObj.optJSONObject(name);
	}

	public JSONArray getJSONArray(String name) {
		return jsonObj.optJSONArray(name);
	}

	public DSObject getObject(String name) {
		if (jsonObj.has(name)) {
			DSObject cpObject = new DSObject(name);
			return cpObject.fromJson(jsonObj.optJSONObject(name));
		}
		return null;
	}

	public DSObject[] getArray(String name) {
		while (jsonObj.has(name)) {
			JSONArray array = jsonObj.optJSONArray(name);
			if (array == null) {
				break;
			}
			int len = array.length();
			DSObject[] objs = new DSObject[len];
			for (int i = 0; i < len; i++) {
				objs[i] = new DSObject(name).fromJson(array.optJSONObject(i));
			}
			return objs;
		}
		return null;
	}

	public ArrayList<DSObject> getList(String name) {
		while (jsonObj.has(name)) {
			JSONArray array = jsonObj.optJSONArray(name);
			if (array == null) {
				break;
			}
			int len = array.length();
			ArrayList<DSObject> list = new ArrayList<DSObject>(len);
			for (int i = 0; i < len; i++) {
				list.add(new DSObject(name).fromJson(array.optJSONObject(i)));
			}
			return list;
		}
		return null;
	}

	public DSObject(Parcel in) {
		this.objName = in.readString();
		String jsonStr = in.readString();
		try {
			jsonObj = new JSONObject(jsonStr);
		} catch (JSONException e) {
			jsonObj = new JSONObject();
			printException(e);
		}
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		String jsonStr = new JSONObject().toString();
		if (jsonObj != null) {
			jsonStr = jsonObj.toString();
		}
		dest.writeString(objName);
		dest.writeString(jsonStr);
	}

	public DSObject fromJson(JSONObject json) {
		this.jsonObj = json;
		return this;
	}
	
	public DSObject fromJson(Object obj){
		if(obj instanceof JSONObject){
			this.jsonObj = (JSONObject) obj;
		}
		return this;
	}

	public DSObject fromJson(String json) {
		try {
			jsonObj = new JSONObject(json);
		} catch (JSONException e) {
			jsonObj = new JSONObject();
			printException(e);
		}
		return this;
	}
	
	private void printException(Exception e){
		if (e != null) {
			Log.e(TAG, e.getLocalizedMessage());
		}
	}

}
