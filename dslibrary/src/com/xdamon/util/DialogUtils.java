package com.xdamon.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

import com.xdamon.widget.BeautifulProgressDialog;

public class DialogUtils {

	public static BeautifulProgressDialog showProgressDialog(Context context, String message,
			DialogInterface.OnCancelListener listener, boolean cancelable) {
		BeautifulProgressDialog progressDialog = new BeautifulProgressDialog(context);
		progressDialog.setCancelable(cancelable);
		progressDialog.setOnCancelListener(listener);
		progressDialog.setMessage(message);
		progressDialog.show();
		return progressDialog;
	}

	public static AlertDialog showAlert(final Context context, final String msg, final String title, final String yes,
			final boolean cancelable, final DialogInterface.OnClickListener lOk) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(yes, lOk);
		builder.setCancelable(cancelable);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlert(final Context context, final String msg, final String title, final String yes,
			final String no, final boolean cancelable, final DialogInterface.OnClickListener lOk,
			final DialogInterface.OnClickListener lCancel) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(yes, lOk);
		builder.setNegativeButton(no, lCancel);
		builder.setCancelable(cancelable);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}
}
