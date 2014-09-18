package com.xdamon.ext;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public abstract class DSHandler extends Handler {
    private WeakReference<Context> mOuter;

    public DSHandler(Context context) {
        mOuter = new WeakReference<Context>(context);
    }

    public DSHandler(Context context, Looper looper) {
        super(looper);
        mOuter = new WeakReference<Context>(context);
    }

    public DSHandler(Context context, Looper looper, Callback callback) {
        super(looper, callback);
        mOuter = new WeakReference<Context>(context);
    }

    @Override
    public final void handleMessage(Message msg) {
        Context context = mOuter.get();
        do {
            if ((context instanceof Activity) && !((Activity) context).isFinishing()) {
                handleRealMessage(msg);
                break;
            }
            if (context != null) {
                handleRealMessage(msg);
                break;
            }
            removeCallbacksAndMessages(null);
        } while (false);
    }

    public abstract void handleRealMessage(Message msg);

}
