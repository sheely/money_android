package com.wanlonggroup.caiplus.model;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.damon.ds.app.DSObject;


public class AccountService {

	private Context mContext;
	private SharedPreferences prefs;
	private final ArrayList<AccountListener> listeners = new ArrayList<AccountListener>();

	public AccountService(Context context) {
		mContext = context;
		prefs = mContext.getSharedPreferences("account", Context.MODE_PRIVATE);
	}

	public String name() {
		return prefs.getString("name", "");
	}

	public String id() {
		return prefs.getString("userId", "");
	}

	public String headIcon() {
		return prefs.getString("headIcon", "");
	}

	public String sessionId() {
		return prefs.getString("sessionId", "");
	}

	public void update(DSObject userProfile) {
		String id = id();
		if (userProfile != null) {
			prefs.edit().putString("name", userProfile.getString("name")).putString(
				"userId", userProfile.getString("userId")).putString(
				"headIcon", userProfile.getString("headIcon")).putString(
				"sessionId", userProfile.getString("sessionId")).commit();
		}
		if(!id.equals(id())){
			dispatchAccountChanged();
		}
	}
	
	public void logout(){
		String id = id();
		prefs.edit().clear().commit();
		if(!TextUtils.isEmpty(id)){
			dispatchAccountChanged();
		}
	}
	
	public void addListener(AccountListener listener){
		listeners.add(listener);
	}
	
	public void removeListener(AccountListener listener){
		listeners.remove(listener);
	}
	
	private void dispatchAccountChanged(){
		for (AccountListener listener : listeners) {
			listener.onAccountChanged(this);
		}
	}
	
	public static interface AccountListener{
		void onAccountChanged(AccountService sender);
	}

}
