package com.wanlonggroup.caiplus.bz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;

public class CyListActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cy_list_activity);
		Fragment fragment = Fragment.instantiate(this, CyListFragment.class.getName(), getIntent().getExtras());
		
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.root_view, fragment, "cy_query_list");
		transaction.commitAllowingStateLoss();
		getSupportFragmentManager().executePendingTransactions();
	}

}
