package com.damon.ds.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.widget.Toast;

import com.damon.ds.library.R;
import com.damon.ds.util.DialogUtils;
import com.damon.ds.widget.BeautifulProgressDialog;
import com.damon.ds.widget.DSActionBar;

public class DSActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DSApplication.instance().activityOnCreate(this);

		if (actionBarType() == ActionBarType.NONE) {
			getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			setContentView(new ViewStub(this));
		} else if (actionBarType() == ActionBarType.CONTENT_DSACTIONBAR) {
			ViewStub stub = (ViewStub) findViewById(R.id.action_bar_stub);
			if (stub != null) {
				stub.inflate();
				initActionBar();
			}
		} else if (actionBarType() == ActionBarType.DSACTIONBAR) {
			getWindow().requestFeature(Window.FEATURE_CUSTOM_TITLE);
			setContentView(new ViewStub(this));
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.ds_action_bar);

			initActionBar();
		}
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

	// ----actionbar-----

	protected enum ActionBarType {
		DSACTIONBAR, NONE, CONTENT_DSACTIONBAR
	}

	private DSActionBar actionBar;

	protected ActionBarType actionBarType() {
		return ActionBarType.DSACTIONBAR;
	}

	protected DSActionBar actionBar() {
		return actionBar;
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		actionBar.setTitle(title);
	}

	public void initActionBar() {
		actionBar = (DSActionBar) findViewById(R.id.ds_action_bar);
		actionBar.setBackgroundColor(getResources().getColor(R.color.actionbarBackground));
		actionBar.setHomeAsUpResource(R.drawable.ic_navi_back);
		actionBar.setHomeAsUpListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		setTitle(getTitle());
	}

	// -----toast and dialog----

	public void showShortToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	private BeautifulProgressDialog progressDialog;
	int progressDialogCount;

	public void showProgressDialog() {
		showProgressDialog("加载中...");
	}

	public void showProgressDialog(String message) {
		if (progressDialog == null || !progressDialog.isShowing()) {
			progressDialog = DialogUtils.showProgressDialog(this, message, new DialogInterface.OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					onProgressDialogCancel();

				}
			}, true);
		} else {
			progressDialog.setMessage(message);
		}
		progressDialogCount++;
	}

	public void dismissProgressDialog() {
		progressDialogCount--;
		if (progressDialogCount == 0 && progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	public void onProgressDialogCancel() {

	}

	public void showAlert(String message) {
		showAlert("提示", message, false, null, null);
	}

	private void showAlert(String title, String message, boolean hasCancelBtn, DialogInterface.OnClickListener lOk,
			DialogInterface.OnClickListener lCancel) {

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
		if (lOk == null) {
			lOk = listener;
		}
		if (lCancel == null) {
			lCancel = listener;
		}
		if (!hasCancelBtn) {
			DialogUtils.showAlert(this, message, title, "确定", false, lOk);
		} else {
			DialogUtils.showAlert(this, message, title, "确定", "取消", false, lOk, lCancel);
		}
	}

}
