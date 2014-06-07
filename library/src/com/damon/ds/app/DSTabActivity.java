package com.damon.ds.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.damon.ds.library.R;
import com.damon.ds.widget.TabIndicator;

public class DSTabActivity extends DSActivity {
	
	protected TabHost mTabHost;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
	}
	
	
	
	@Override
	protected void onSetContent() {
		setContentView(R.layout.ds_tab_activity);
	}
	
	public void addTab(String title, Intent intent) {
		addTab(title, 0, 0, intent);
	}
	
	public void addTab(String title, int indicatorView,Intent intent) {
		addTab(title, 0, indicatorView, intent);
	}

	public void addTab(String title, int icon, int indicatorView, Intent intent) {
		if (title == null) {
			throw new IllegalArgumentException("title cann't be null!");
		}
		
		if (intent == null) {
			throw new IllegalArgumentException("intent cann't be null!");
		}

		TabHost.TabSpec spec = mTabHost.newTabSpec(title)
				.setIndicator(new TabIndicator(this, title, icon, indicatorView).createIndicatorView(mTabHost))
				.setContent(intent);
		mTabHost.addTab(spec);
	}

}
