package com.wanlonggroup.caiplus.model;

import org.json.JSONObject;

import com.damon.ds.app.DSObject;

public class CPObject extends DSObject {

	public CPObject() {
		super();
	}

	public CPObject(String objName) {
		super(objName);
	}

	@Override
	public CPObject fromJson(JSONObject json) {
		super.fromJson(json);
		return this;
	}

	@Override
	public CPObject fromJson(Object obj) {
		super.fromJson(obj);
		return this;
	}

	@Override
	public CPObject fromJson(String json) {
		super.fromJson(json);
		return this;
	}
}
