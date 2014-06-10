package com.damon.ds.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class DSActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DSApplication.instance().activityOnCreate(this);
	}

	public void addFramgent(Class<?> clss, Bundle args) {
		addFramgent(android.R.id.content, null, clss, args);
	}

	public void addFramgent(String tag, Class<?> clss, Bundle args) {
		addFramgent(android.R.id.content, tag, clss, args);
	}

	public void addFramgent(int containerViewId, String tag, Class<?> clss, Bundle args) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		Fragment fragment = Fragment.instantiate(this, clss.getName(), args);
		transaction.add(containerViewId, fragment, tag);
		transaction.addToBackStack(null);
		transaction.commitAllowingStateLoss();
		getSupportFragmentManager().executePendingTransactions();
	}

	public boolean popFragment() {
		return popFragment(android.R.id.content);
	}

	public boolean popFragment(int containerViewId) {
		Fragment fragment = getSupportFragmentManager().findFragmentById(containerViewId);
		if (fragment != null) {
			return getSupportFragmentManager().popBackStackImmediate();
		}
		return false;
	}

	public boolean popFragment(String tag) {
		Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
		if (fragment != null) {
			return getSupportFragmentManager().popBackStackImmediate();
		}
		return false;
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

	public void startActivity(String urlSchema) {
		startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(urlSchema)), -1);
	}

	public void startActivity(String urlSchema, int requestCode) {
		startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(urlSchema)), requestCode);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}

}
