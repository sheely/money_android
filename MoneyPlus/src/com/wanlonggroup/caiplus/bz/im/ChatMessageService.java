package com.wanlonggroup.caiplus.bz.im;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.xdamon.executor.ThreadExecutorsHelper;

public class ChatMessageService extends Service {

    public static final int MESSAGE_RECEIVER_INTERVAL = 5000;

    public static final String ACTION_START = "com.wanlonggroup.caiplus.bz.im.MessageService_start";

    public static final String ACTION_STOP = "com.wanlonggroup.caiplus.bz.im.MessageService_stop";

    private StartMessageThread messageThread;

    public static void start(Context context) {
        Intent intent = new Intent(context, ChatMessageService.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, ChatMessageService.class);
        intent.setAction(ACTION_STOP);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (ACTION_START.equals(intent.getAction())) {
                startMsgThread();
            } else if (ACTION_STOP.equals(intent.getAction())) {
                stopMsgThread();
                stopSelf();
            }
        }
        return START_STICKY;
    }

    private void stopMsgThread() {
        if (messageThread != null) {
            ThreadExecutorsHelper.cancel(messageThread, true);
            messageThread = null;
        }
    }

    private void startMsgThread() {
        stopMsgThread();
        messageThread = new StartMessageThread();
        ThreadExecutorsHelper.scheduleWithFixedDelay(messageThread, MESSAGE_RECEIVER_INTERVAL);
    }

    class StartMessageThread implements Runnable {
        public void run() {
            ChatMessageBrigdeService.startService(ChatMessageService.this);
        }
    }

}
