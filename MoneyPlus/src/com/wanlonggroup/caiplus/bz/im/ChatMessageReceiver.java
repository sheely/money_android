package com.wanlonggroup.caiplus.bz.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xdamon.util.NetworkUtils;

public class ChatMessageReceiver extends BroadcastReceiver {

	public final static String ACTION_MESSAGE = "com.wanlonggroup.caiplus.bz.im.Message";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
			if(NetworkUtils.isConnectNetwork(context)){
//				ChatMessageService.start(context);
			}
		}
	}
}
