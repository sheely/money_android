package com.damon.ds.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Toast;

import com.damon.ds.library.R;
import com.damon.ds.util.DialogUtils;
import com.damon.ds.widget.BeautifulProgressDialog;
import com.damon.ds.widget.DSActionBar;

public class DSFragment extends Fragment {

	private DSActivity dsActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof DSActivity)) {
			throw new IllegalArgumentException("DSFragment must attach to DSActivity!");
		}
		dsActivity = (DSActivity) activity;
	}

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
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initActionBar(view);
	}

	protected void setTitle(String title) {
		if (actionBar != null) {
			actionBar.setTitle(title);
		}
	}

	@Override
	public void onDestroy() {
		if (toast != null) {
			toast.cancel();
		}
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		super.onDestroy();
	}

	// ---actionbar----
	private DSActionBar actionBar;

	protected boolean hasActionBar() {
		return false;
	}

	protected final DSActionBar actionBar() {
		return actionBar;
	}

	private void initActionBar(View actionBarContainerView) {
		if (hasActionBar() || actionBarEnable) {
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
							if (dsActivity != null && !dsActivity.isFinishing()) {
								if (!dsActivity.popFragment()) {
									dsActivity.finish();
								}
							}
						}
					});
				}
			}
		}
	}

	private boolean actionBarEnable = false;

	public final void setActionBarEnable(boolean enable) {
		actionBarEnable = enable;
		if (actionBar != null) {
			actionBar.setVisibility(enable ? View.VISIBLE : View.GONE);
		}
	}

	// -----toast and dialog----

	Toast toast;

	public void showShortToast(String message) {
		if (dsActivity == null || dsActivity.isFinishing()) {
			return;
		}
		if (toast == null) {
			toast = Toast.makeText(dsActivity, message, Toast.LENGTH_SHORT);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	private BeautifulProgressDialog progressDialog;
	int progressDialogCount;

	public void showProgressDialog() {
		showProgressDialog("加载中...");
	}

	public void showProgressDialog(String message) {
		if (dsActivity == null || dsActivity.isFinishing()) {
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
		if (dsActivity == null || dsActivity.isFinishing()) {
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

}
