package com.wanlonggroup.caiplus.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.damon.ds.app.DSFragment;
import com.damon.ds.widget.DSActionBar;
import com.nineoldandroids.animation.ObjectAnimator;
import com.wanlonggroup.caiplus.R;

public class BaseFragment extends DSFragment {

	private DSActionBar actionBar;
	private ObjectAnimator objectAnimator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return onSetView(inflater, container);
	}

	public View onSetView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(android.R.layout.simple_list_item_1, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (hasActionBar()) {
			ViewStub stub = (ViewStub) view.findViewById(R.id.action_bar_stub);
			if (stub != null) {
				stub.inflate();
				actionBar = (DSActionBar) view.findViewById(R.id.ds_action_bar);
				if (actionBar != null) {
					actionBar.setBackgroundColor(getResources().getColor(R.color.actionbarBackground));
					actionBar.setHomeAsUpResource(R.drawable.ic_navi_back);
				}
			}
		}
	}

	protected boolean hasActionBar() {
		return true;
	}

	protected void showActionBar() {
		if (actionBar != null) {
			objectAnimator = ObjectAnimator.ofFloat(actionBar, "y", 0);
			objectAnimator.setDuration(1000);
			objectAnimator.start();
		}
	}

	protected void hideActionBar() {
		if (actionBar != null) {
			objectAnimator = ObjectAnimator.ofFloat(actionBar, "y", -actionBar.getHeight());
			objectAnimator.setDuration(1000);
			objectAnimator.start();
		}
	}

	protected DSActionBar actionBar() {
		return actionBar;
	}

	public void setActionBarEnable(boolean enable) {
		if (actionBar != null) {
			actionBar.setVisibility(enable ? View.VISIBLE : View.GONE);
		}
	}

}
