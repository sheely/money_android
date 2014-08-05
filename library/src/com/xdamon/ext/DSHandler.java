package com.xdamon.ext;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

import com.xdamon.app.DSActivity;

public abstract class DSHandler extends Handler {
	private WeakReference<DSActivity> mOuter;

	public DSHandler(DSActivity activity) {
		mOuter = new WeakReference<DSActivity>(activity);
	}

	@Override
	public final void handleMessage(Message msg) {
		if (mOuter.get() != null && !mOuter.get().isFinishing()) {
			handleRealMessage(msg);
		}else{
			removeCallbacksAndMessages(null);
		}
	}

	public abstract void handleRealMessage(Message msg);

}
