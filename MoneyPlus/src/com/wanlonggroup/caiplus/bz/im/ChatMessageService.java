package com.wanlonggroup.caiplus.bz.im;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.next.util.Log;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.wanlonggroup.caiplus.util.ConfigSwitch;
import com.xdamon.app.DSObject;
import com.xdamon.executor.ThreadExecutorsHelper;
import com.xdamon.util.DSObjectFactory;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.NoSubscriberEvent;

public class ChatMessageService extends Service {

    public static final int MESSAGE_RECEIVER_INTERVAL = 3000;

    public static final String ACTION_START = "com.wanlonggroup.caiplus.bz.im.MessageService_start";

    public static final String ACTION_STOP = "com.wanlonggroup.caiplus.bz.im.MessageService_stop";

    private GetMessageThread messageThread;

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
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        return super.onStartCommand(intent, flags, startId);
    }

    public void onEvent(NoSubscriberEvent event) {
        if (event.originalEvent instanceof IMessaging) {
            EventBus.getDefault().post(((IMessaging) event.originalEvent).toIMessage());
        } else if (event.originalEvent instanceof IMessage) {
            EventBus.getDefault().addEvent(event.originalEvent);
        }
    }

    private void stopMsgThread() {
        if (messageThread != null) {
            ThreadExecutorsHelper.cancel(messageThread, true);
            messageThread = null;
        }
    }

    private void startMsgThread() {
        stopMsgThread();
        messageThread = new GetMessageThread();
        ThreadExecutorsHelper.scheduleWithFixedDelay(messageThread, MESSAGE_RECEIVER_INTERVAL);
    }

    class GetMessageThread implements Runnable {
        public void run() {
            Log.i("GetMessageThread", "GetMessageThread");
            SHPostTaskM receiveMsgTask = new SHPostTaskM();
            receiveMsgTask.setUrl(ConfigSwitch.instance().wrapDomain(
                "http://cjcapp.nat123.net:21414/myStruts1/miReceiveMessage.do"));
            receiveMsgTask.setListener(new ITaskListener() {

                @Override
                public void onTaskFinished(SHTask task) throws Exception {
                    DSObject dsChatMessages = DSObjectFactory.create(
                        CPModeName.CY_CHAT_MESSAGE_LIST).fromJson(task.getResult());
                    for (DSObject dsMsg : dsChatMessages.getArray(CPModeName.CY_CHAT_MESSAGE_LIST,
                        CPModeName.CY_CHAT_MESSAGE_ITEM)) {
                        Log.i(
                            "GetMessageThread",
                            dsMsg.getString("senderuserid") + ">>>"
                                    + dsMsg.getString("chatcontent"));

                        IMessaging messaging = new IMessaging(dsMsg.getString("senderuserid"),
                                dsMsg.getString("senderusername"),
                                dsMsg.getString("senderheadicon"),
                                dsMsg.getString("receiveruserid"), dsMsg.getString("chatcontent"),
                                dsMsg.getString("sendtime"));
                        EventBus.getDefault().post(messaging);
                    }
                }

                @Override
                public void onTaskFailed(SHTask task) {
                    if (task != null && task.getRespInfo() != null
                            && task.getRespInfo().getMessage() != null)
                        Log.e("GetMessageThread", task.getRespInfo().getMessage());
                }

                @Override
                public void onTaskUpdateProgress(SHTask task, int count, int total) {

                }

                @Override
                public void onTaskTry(SHTask task) {

                }

            });
            receiveMsgTask.start();
        }
    }

}
