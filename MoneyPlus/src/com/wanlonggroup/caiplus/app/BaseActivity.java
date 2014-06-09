package com.wanlonggroup.caiplus.app;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;

import com.damon.ds.actionbar.DSActionBar;
import com.damon.ds.app.DSActivity;
import com.wanlonggroup.caiplus.R;

public class BaseActivity extends DSActivity {

	private DSActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(new ViewStub(this));
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.ds_action_bar);

		actionBar = (DSActionBar) findViewById(R.id.ds_action_bar);
		actionBar.setHomeAsUpListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		setTitle(getTitle());
	}

	protected DSActionBar getDSActionBar() {
		return actionBar;
	}
	
	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		actionBar.setTitle(title);
	}

}
