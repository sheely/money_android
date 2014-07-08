package com.wanlonggroup.caiplus.bz;

import android.os.Bundle;
import android.view.View;

import com.damon.ds.widget.DSActionBar;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BasePtrListFragment;

public class CyHomeFragment extends BasePtrListFragment implements View.OnClickListener{
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		actionBar().setDisplayHomeAsUpEnabled(false);
		setTitle("财友");
		invalidateActionBar();
	}
	
	@Override
	protected boolean hasActionBar() {
		return true;
	}
	
	@Override
	public void onCreateActionBar(DSActionBar actionBar) {
		actionBar.addAction(R.drawable.ic_search, "ic_search", this);
	}

	@Override
	public void onClick(View v) {
		
	}

}
