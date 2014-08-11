package com.wanlonggroup.caiplus.bz.im;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.xdamon.app.DSObject;

public class ChatHelper {
	
	private Context mContext;

	private static ChatHelper _instance;
	
	private ArrayList<DSObject> dsCyChatingList = new ArrayList<DSObject>();
	
	private DSObject dsCyCurrentChating;

	private ChatHelper(Context context) {
		mContext = context;
	}

	public static ChatHelper instance(Context context) {
		if (_instance == null) {
			_instance = new ChatHelper(context);
		}
		return _instance;
	}

	public void chat2Who(final Context context, DSObject dscaiyou) {
		dsCyCurrentChating = dscaiyou;
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://chat"));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("caiyou", dscaiyou);
		context.startActivity(intent);
	}
	
	public void addChatMessage(DSObject dsChatMessage){
		
	}

}
