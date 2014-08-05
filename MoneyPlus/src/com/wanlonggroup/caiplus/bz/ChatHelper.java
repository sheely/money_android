package com.wanlonggroup.caiplus.bz;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.xdamon.app.DSObject;

public class ChatHelper {

	private static ChatHelper _instance;

	private ChatHelper() {

	}

	public static ChatHelper instance() {
		if (_instance == null) {
			_instance = new ChatHelper();
		}
		return _instance;
	}

	public void chat2Who(final Context context, DSObject dscaiyou) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://chat"));
		intent.putExtra("caiyou", dscaiyou);
		context.startActivity(intent);
	}
	
	

}
