package com.damon.ds.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class DSActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		onHandleIntent();
		onSetContent();
		DSApplication.instance().activityOnCreate(this);
	}
	
	protected void onHandleIntent(){
		
	}
	
	protected void onSetContent(){
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		DSApplication.instance().activityOnResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		DSApplication.instance().activityOnPause(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		DSApplication.instance().activityOnDestory(this);
	}
	
	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}

}
