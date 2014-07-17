package com.wanlonggroup.caiplus.bz;

import android.view.View;
import android.view.View.OnClickListener;

import com.damon.ds.widget.DSActionBar;
import com.wanlonggroup.caiplus.app.BasePtrListActivity;

public class MyCalenderActivity extends BasePtrListActivity implements OnClickListener{
	
	@Override
	public void onCreateActionBar(DSActionBar actionBar) {
		super.onCreateActionBar(actionBar);
		actionBar.addAction("新增", "add_calender", this);
	}
	
	@Override
	public void onClick(View v) {
		
	}

}
