package com.wanlonggroup.caiplus.home;

import android.os.Bundle;
import android.view.View;

import com.wanlonggroup.caiplus.app.BasePtrListFragment;

public class CyHomeFragment extends BasePtrListFragment {
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		actionBar().setDisplayHomeAsUpEnabled(false);
	}

}
