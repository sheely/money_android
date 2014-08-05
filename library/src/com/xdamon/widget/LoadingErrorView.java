package com.xdamon.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdamon.library.R;

public class LoadingErrorView extends LinearLayout implements
		View.OnClickListener {

	TextView errorText;
	LoadRetry callback;
	int type = 1; // type = 1 is loading agent, and the icon is on the left.
					// type = 2 is loading fragment, and the icon is on the top
	static final String DEFAULT_ERROR_MESSAGE = "网络连接失败 点击重新加载";

	public LoadingErrorView(Context context) {
		super(context);
	}

	public LoadingErrorView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		errorText = (TextView) this.findViewById(android.R.id.text1);
		errorText.setText(DEFAULT_ERROR_MESSAGE);
		if (this.type == 2) {
			Drawable drawable = getResources().getDrawable(
					R.drawable.icon_loading_big);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			errorText.setCompoundDrawables(null, drawable, null, null);
		} else {
			Drawable drawable = getResources().getDrawable(
					R.drawable.icon_loading_small);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			errorText.setCompoundDrawables(drawable, null, null, null);
		}
		this.setOnClickListener(this);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}

	public interface LoadRetry {
		public void loadRetry(View v);
	}

	public void setErrorMessage(String errorMsg) {
		if (errorText != null) {
			errorText.setText(errorMsg);
		}
	}

	public void setCallBack(LoadRetry callback) {
		this.callback = callback;
	}

	@Override
	public void onClick(View v) {
		if (callback != null)
			callback.loadRetry(v);
	}

	public void setType(int type) {
		if (type == 2) {
			Drawable drawable = getResources().getDrawable(
					R.drawable.icon_loading_big);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			errorText.setCompoundDrawables(null, drawable, null, null);
		} else {
			Drawable drawable = getResources().getDrawable(
					R.drawable.icon_loading_small);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			errorText.setCompoundDrawables(drawable, null, null, null);
		}
		this.type = 2;
	}
}
