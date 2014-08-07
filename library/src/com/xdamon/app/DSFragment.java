package com.xdamon.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Toast;

import com.xdamon.library.R;
import com.xdamon.app.DSActivity.ActionBarType;
import com.xdamon.util.DialogUtils;
import com.xdamon.util.URLBase64;
import com.xdamon.widget.BeautifulProgressDialog;
import com.xdamon.widget.DSActionBar;

public class DSFragment extends Fragment {

	private DSActivity dsActivity;

	private CharSequence activiyTitle;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof DSActivity)) {
			throw new IllegalArgumentException("DSFragment must attach to DSActivity!");
		}
		dsActivity = (DSActivity) activity;
		activiyTitle = dsActivity.getTitle();
	}

	protected DSActivity getDSActivity() {
		return dsActivity;
	}

	public boolean isActivityFinish() {
		if (dsActivity == null || dsActivity.isFinishing()) {
			return true;
		}
		return false;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		dsActivity.setTitle(activiyTitle);
	}

	public boolean canBack() {
		return true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return onSetView(inflater, container);
	}

	public View onSetView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(android.R.layout.simple_list_item_1, container, false);
	}

	public void startActivity(String urlSchema) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlSchema));
		startActivity(intent);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initActionBar(view);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		invalidateActionBar();
	}
	
	protected void setTitle(String title) {
		if (actionBar != null) {
			actionBar.setTitle(title);
		}
	}

	@Override
	public void onDestroy() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		super.onDestroy();
	}

	public void finish() {
		if (canBack()) {
			if (!getFragmentManager().popBackStackImmediate()) {
				getActivity().finish();
			}
		}
	}

	// ---actionbar----
	private DSActionBar actionBar;

	protected boolean hasActionBar() {
		return false;
	}

	protected final DSActionBar actionBar() {
		return actionBar;
	}

	public final void invalidateActionBar() {
		if (actionBar != null) {
			actionBar.removeAllAction();
			onCreateActionBar(actionBar);
		}
	}

	public void onCreateActionBar(DSActionBar actionBar) {

	}

	private void initActionBar(View actionBarContainerView) {
		if (dsActivity.actionBarType() == ActionBarType.DSACTIONBAR) {
			actionBar = dsActivity.actionBar();
		} else if (hasActionBar()) {
			ViewStub stub = (ViewStub) actionBarContainerView.findViewById(R.id.action_bar_stub);
			if (stub != null) {
				stub.inflate();
				actionBar = (DSActionBar) actionBarContainerView.findViewById(R.id.ds_action_bar);
				if (actionBar != null) {
					actionBar.setBackgroundColor(getResources().getColor(R.color.actionbarBackground));
					actionBar.setHomeAsUpResource(R.drawable.ic_navi_back);
					actionBar.setHomeAsUpListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							if (!isActivityFinish()) {
								dsActivity.onBackPressed();
							}
						}
					});
				}
			}
		} else {
			actionBar = (DSActionBar) LayoutInflater.from(dsActivity).inflate(R.layout.ds_action_bar, null, false);

		}
	}

	// -----toast and dialog----

	public void showShortToast(String message) {
		if (isActivityFinish()) {
			return;
		}
		Toast.makeText(dsActivity, message, Toast.LENGTH_SHORT).show();
	}

	private BeautifulProgressDialog progressDialog;
	int progressDialogCount;

	public void showProgressDialog() {
		showProgressDialog("加载中...");
	}

	public void showProgressDialog(String message) {
		if (isActivityFinish()) {
			return;
		}
		if (progressDialog == null || !progressDialog.isShowing()) {
			progressDialog = DialogUtils.showProgressDialog(dsActivity, message,
				new DialogInterface.OnCancelListener() {

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
		if (progressDialogCount < 0) {
			progressDialogCount = 0;
		}
		if (progressDialogCount == 0 && progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	public void onProgressDialogCancel() {

	}

	public void showAlert(String message) {
		showAlert("提示", message, false, null, null);
	}

	public void showAlert(String title, String message, boolean hasCancelBtn, DialogInterface.OnClickListener lOk,
			DialogInterface.OnClickListener lCancel) {
		if (isActivityFinish()) {
			return;
		}

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
			DialogUtils.showAlert(dsActivity, message, title, "确定", false, lOk);
		} else {
			DialogUtils.showAlert(dsActivity, message, title, "确定", "取消", false, lOk, lCancel);
		}
	}

	// ====get parameters

	public int getIntParam(String name, int defaultValue) {
		Bundle bundle = getArguments();
		if (bundle != null) {
			try {
				String val = bundle.getString(name);
				return Integer.parseInt(val);
			} catch (Exception e) {
			}
			return bundle.getInt(name, defaultValue);
		}
		return defaultValue;
	}

	public int getIntParam(String name) {
		return getIntParam(name, 0);
	}

	public String getStringParam(String name, String defaultValue) {
		Bundle bundle = getArguments();
		if (bundle != null) {
			return bundle.getString(name);
		}
		return defaultValue;
	}

	public String getStringParam(String name) {
		return getStringParam(name, null);
	}

	public double getDoubleParam(String name, double defaultValue) {
		Bundle bundle = getArguments();
		if (bundle != null) {
			try {
				String val = bundle.getString(name);
				return Double.parseDouble(val);
			} catch (Exception e) {
			}
			return bundle.getDouble(name, defaultValue);
		}
		return defaultValue;
	}

	public double getDoubleParam(String name) {
		return getDoubleParam(name, 0);
	}

	public DSObject getObjectParam(String name) {
		Bundle bundle = getArguments();
		if (bundle != null) {
			try {
				String val = bundle.getString(name);
				if (val != null) {
					byte[] bytes = URLBase64.decode(val);
					return new DSObject(bytes);
				}
			} catch (Exception e) {
			}

			return bundle.getParcelable(name);
		}
		return null;
	}

}
