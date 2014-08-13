package com.xdamon.util;

import org.json.JSONObject;

import com.xdamon.app.DSObject;

public class DSObjectFactory {

	public static DSObject create(String objName, Object obj) {
		return new DSObject(objName).fromJson(obj);
	}

	public static DSObject create(String objName) {
		return new DSObject(objName);
	}

	public static DSObject create(String objName, JSONObject json) {
		return new DSObject(objName).fromJson(json);
	}

	public static DSObject create(String objName, String json) {
		return new DSObject(objName).fromJson(json);
	}
}
