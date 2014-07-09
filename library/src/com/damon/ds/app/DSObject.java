package com.damon.ds.app;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.damon.ds.io.DSStreamReader;
import com.damon.ds.io.DSStreamWriter;

public class DSObject implements Parcelable {

	public static final String TAG = "DSObject";

	protected JSONObject jsonObj;
	protected String objName;

	public DSObject(String objName) {
		this.objName = objName;
		jsonObj = new JSONObject();
	}

	public DSObject(byte[] bytes) {
		this(bytes, 0, bytes.length);
	}

	public DSObject(byte[] bytes, int offset, int lenght) {
		DSStreamReader dr = new DSStreamReader(bytes, offset, lenght);
		try {
			objName = dr.readString();
			jsonObj = new JSONObject(dr.readString());
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
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

	public JSONObject getRootJsonObject() {
		return jsonObj;
	}

	public String getString(String name) {
		return jsonObj.optString(name);
	}

	public DSObject put(String name, String value) {
		try {
			this.jsonObj.put(name, value);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}

	public int getInt(String name) {
		return jsonObj.optInt(name);
	}

	public DSObject put(String name, int value) {
		try {
			this.jsonObj.put(name, value);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}

	public long getLong(String name) {
		return jsonObj.optLong(name);
	}

	public DSObject put(String name, long value) {
		try {
			this.jsonObj.put(name, value);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}

	public Object getObject(String name) {
		return jsonObj.opt(name);
	}

	public DSObject put(String name, Object value) {
		try {
			this.jsonObj.put(name, value);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}

	public double getDouble(String name) {
		return jsonObj.optDouble(name);
	}

	public DSObject put(String name, double value) {
		try {
			this.jsonObj.put(name, value);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}

	public boolean getBoolean(String name) {
		return jsonObj.optBoolean(name);
	}

	public DSObject put(String name, boolean value) {
		try {
			this.jsonObj.put(name, value);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}

	public JSONObject getJSONObject(String name) {
		return jsonObj.optJSONObject(name);
	}

	public DSObject put(String name, JSONObject json) {
		try {
			jsonObj.put(name, json);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}

	public JSONArray getJSONArray(String name) {
		return jsonObj.optJSONArray(name);
	}

	public DSObject put(String name, JSONArray arr) {
		try {
			jsonObj.put(name, arr);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}

	public DSObject getDSObject(String name) {
		return getDSObject(name, name);
	}

	public DSObject getDSObject(String name, String objName) {
		if (jsonObj.has(name)) {
			if (TextUtils.isEmpty(objName)) {
				objName = name;
			}
			DSObject cpObject = new DSObject(name);
			return cpObject.fromJson(jsonObj.optJSONObject(name));
		}
		return null;
	}

	public DSObject put(DSObject obj) {
		try {
			if (TextUtils.isEmpty(obj.objName)) {
				throw new IllegalArgumentException("the obj must has ojbname");
			}
			jsonObj.put(obj.objName, obj.jsonObj);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}

	public DSObject[] getArray(String name) {
		return getArray(name, name);
	}

	public DSObject[] getArray(String name, String objName) {
		while (jsonObj.has(name)) {
			JSONArray array = jsonObj.optJSONArray(name);
			if (array == null) {
				break;
			}
			if (TextUtils.isEmpty(objName)) {
				objName = name;
			}
			int len = array.length();
			DSObject[] objs = new DSObject[len];
			for (int i = 0; i < len; i++) {
				try {
					Object tmp = array.opt(i);
					if (tmp instanceof String) {
						JSONObject obj = new JSONObject();
						obj.put(objName, tmp);
						objs[i] = new DSObject(objName).fromJson(obj);
					} else {
						objs[i] = new DSObject(objName).fromJson(array.optJSONObject(i));
					}
				} catch (Exception e) {
					objs[i] = new DSObject(objName).fromJson(array.optJSONObject(i));

				}
			}
			return objs;
		}
		return null;
	}

	public DSObject put(DSObject[] arr) {
		for (DSObject obj : arr) {
			put(obj);
		}
		return this;
	}

	public ArrayList<DSObject> getList(String name) {
		return getList(name, name);
	}

	public ArrayList<DSObject> getList(String name, String objName) {
		while (jsonObj.has(name)) {
			JSONArray array = jsonObj.optJSONArray(name);
			if (array == null) {
				break;
			}
			if (TextUtils.isEmpty(objName)) {
				objName = name;
			}
			int len = array.length();
			ArrayList<DSObject> list = new ArrayList<DSObject>(len);
			for (int i = 0; i < len; i++) {
				try {
					Object tmp = array.opt(i);
					if (tmp instanceof String) {
						JSONObject obj = new JSONObject();
						obj.put(objName, tmp);
						list.add(new DSObject(objName).fromJson(obj));
					} else {
						list.add(new DSObject(objName).fromJson(array.optJSONObject(i)));
					}
				} catch (Exception e) {
					list.add(new DSObject(objName).fromJson(array.optJSONObject(i)));

				}
			}
			return list;
		}
		return null;
	}

	public DSObject put(ArrayList<DSObject> list) {
		for (DSObject obj : list) {
			put(obj);
		}
		return this;
	}

	public byte[] toByteArray() {
		DSStreamWriter dw = new DSStreamWriter();
		try {
			dw.writeString(objName);
			dw.writeString(jsonObj.toString());
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		return dw.toByteArray();
	}

	private DSObject(Parcel in) {
		this.objName = in.readString();
		String jsonStr = in.readString();
		try {
			jsonObj = new JSONObject(jsonStr);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
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
		if (json == null) {
			json = new JSONObject();
		}
		this.jsonObj = json;
		return this;
	}

	public DSObject fromJson(Object obj) {
		if (obj instanceof String) {
			return fromJson((String) obj);
		} else if (obj instanceof JSONObject) {
			this.jsonObj = (JSONObject) obj;
		} else {
			throw new IllegalArgumentException("obj must be instanceof JSONObject");
		}
		return this;
	}

	public DSObject fromJson(String json) {
		try {
			if (!TextUtils.isEmpty(json)) {
				if (!json.startsWith("{")) {
					json = "{" + json + "}";
				}
				jsonObj = new JSONObject(json);
			}
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}

	public <T extends DSObject> T convertToSubInstance(Class<T> classOfT) {
		try {
			T t = classOfT.getDeclaredConstructor(String.class).newInstance(objName);
			t.objName = objName;
			t.jsonObj = jsonObj;
			return t;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public String toString() {
		return objName + ":" + jsonObj.toString();
	}

	@Override
	public int hashCode() {
		return objName.hashCode() + jsonObj.toString().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof DSObject)) {
			return false;
		}
		DSObject an = (DSObject) o;
		if (!objName.equals(an.objName)) {
			return false;
		}
		if (!jsonObj.toString().equals(an.jsonObj.toString())) {
			return false;
		}
		return true;
	}

}
