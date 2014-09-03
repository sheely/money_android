package com.xdamon.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.xdamon.library.R;
import com.xdamon.util.DSUtils;

public class BeautifulProgressDialog extends Dialog {

    Context mContext;
    private View mContent;
    private TextView mMessageText;

    public BeautifulProgressDialog(Context context) {
        super(context, R.style.dialog_fullscreen);
        mContext = context;
        setupView();
    }

    protected void setupView() {
        View view = View.inflate(mContext, R.layout.beautiful_progress_dialog, null);
        mContent = view.findViewById(R.id.content);
        mMessageText = (TextView) view.findViewById(R.id.message);

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

    public void setMessage(CharSequence message) {
        mMessageText.setText(message);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!DSUtils.isPointInsideView(event.getX(), event.getY(), mContent)) {
            return true;
        }
        return super.onTouchEvent(event);
    }
}
