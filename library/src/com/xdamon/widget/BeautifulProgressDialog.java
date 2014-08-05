package com.xdamon.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdamon.library.R;
import com.xdamon.util.DSUtils;

public class BeautifulProgressDialog extends Dialog implements android.view.View.OnClickListener {

	Context mContext;
	private View mDivider;
	private View mContent;
	private TextView mMessageText;
	private ImageView mBtnCancel;
	
	public BeautifulProgressDialog(Context context) {
		super(context, R.style.dialog_fullscreen);
		mContext = context;
		setupView();
	}
	
	protected void setupView() {
		View view = View.inflate(mContext, R.layout.beautiful_progress_dialog, null);
		mDivider = view.findViewById(R.id.divider);
		mContent = view.findViewById(R.id.content);
		mMessageText = (TextView) view.findViewById(R.id.message);
		mBtnCancel = (ImageView) view.findViewById(R.id.btn_cancel);
		mBtnCancel.setOnClickListener(this);
		
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		setContentView(view);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return super.onKeyDown(keyCode, event);
		} 
		return false;
	}

	@Override
    public void setCancelable(boolean flag) {
    	super.setCancelable(flag);
    	if (flag) {
    		mDivider.setVisibility(View.VISIBLE);
    		mBtnCancel.setVisibility(View.VISIBLE);
    	} else {
    		mDivider.setVisibility(View.GONE);
    		mBtnCancel.setVisibility(View.GONE);
    	}
    }
    
    public void setMessage(CharSequence message) {
    	mMessageText.setText(message);
    }

	@Override
	public void onClick(View v) {
		if(v == mBtnCancel){
			cancel();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!DSUtils.isPointInsideView(event.getX(), event.getY(), mContent)) {
			return true;
		}
		return super.onTouchEvent(event);
	}
}
