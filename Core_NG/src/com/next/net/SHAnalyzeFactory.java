package com.next.net;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;

import com.next.app.StandardApplication;

public class SHAnalyzeFactory {
	public static String CORE_NOTIFICATION_LOGIN_TIMEOUT = "core_notification_login_timeout";//  登录超时  session 过期

	public static void analyze(SHTask task, String httpResult)
			throws ParseException, IOException, JSONException {
		if (httpResult != null && httpResult.length() > 0) {
			JSONObject jsResult = new JSONObject(httpResult);
			task.mRespinfo = new SHRespinfo();
			if(jsResult.has("code")){
				task.mRespinfo.setCode(jsResult.getInt("code"));
				if(jsResult.getInt("code") == -5){// seesion过期
					Intent intent = new Intent(
							CORE_NOTIFICATION_LOGIN_TIMEOUT);
					StandardApplication.getInstance().sendBroadcast(intent);
				}
			}
			if(jsResult.has("message")){
				task.mRespinfo.setMessage(jsResult.getString("message"));
			}else if (jsResult.has("msg")){
				task.mRespinfo.setMessage(jsResult.getString("msg"));
			}
			if(task.mRespinfo.getCode() >= 0 && jsResult.has("data")){
				task.mResult = jsResult.get("data");
			}
			if(jsResult.has("meta")){
				task.mExtra = jsResult.get("meta");
			}
		}
	}
}
