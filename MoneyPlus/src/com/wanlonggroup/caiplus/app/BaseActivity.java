package com.wanlonggroup.caiplus.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.widget.Toast;

import com.damon.ds.app.DSActivity;
import com.damon.ds.util.DialogUtils;
import com.damon.ds.widget.BeautifulProgressDialog;
import com.damon.ds.widget.DSActionBar;
import com.wanlonggroup.caiplus.R;

public class BaseActivity extends DSActivity {

	protected enum ActionBarType {
		DSACTIONBAR, NONE
	}

	private DSActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (actionBarType() == ActionBarType.NONE) {
			getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			setContentView(new ViewStub(this));
		} else {
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

			actionBar.setHomeAsUpResource(R.drawable.ic_navi_back);

			setTitle(getTitle());
		}

	}

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

	public void showShortToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	private BeautifulProgressDialog progressDialog;
	int progressDialogCount;
	
	public void showProgressDialog(){
		showProgressDialog("加载中...");
	}

	public void showProgressDialog(String message) {
		if (progressDialog == null || !progressDialog.isShowing()) {
			progressDialog = DialogUtils.showProgressDialog(this, message, new DialogInterface.OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					onProgressDialogCancel();

				}
			}, false);
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
