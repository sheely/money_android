package com.next.net;

import org.json.JSONException;
import org.json.JSONObject;

import com.next.util.SHEnvironment;

public class SHIdentication {
	public static JSONObject getIdentication() {
		JSONObject jsResult = new JSONObject();
		try {
			if (SHEnvironment.getInstance().getSession() != null && SHEnvironment.getInstance().getSession().length() > 0) {
				jsResult.put("type","session" );
				jsResult.put("session_id",SHEnvironment.getInstance().getSession());
			}else {
				jsResult.put("type","basic");
				if(SHEnvironment.getInstance().getLoginID() != null && SHEnvironment.getInstance().getPassword() != null){
					jsResult.put("username",SHEnvironment.getInstance().getLoginID());
					jsResult.put("password",SHEnvironment.getInstance().getPassword());
					jsResult.put("imei","111111");
				}
				jsResult.put("info","systemVersion:"+SHEnvironment.getInstance().getVersion()+ ";systemModel:android");
				jsResult.put("version", SHEnvironment.getInstance().getVersion().toString());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsResult;
	}
}
