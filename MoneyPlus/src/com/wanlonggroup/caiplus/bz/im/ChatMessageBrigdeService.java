package com.wanlonggroup.caiplus.bz.im;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.next.util.Log;
import com.next.util.SHEnvironment;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.wanlonggroup.caiplus.util.ConfigSwitch;
import com.xdamon.app.DSObject;
import com.xdamon.executor.ThreadExecutorsHelper;
import com.xdamon.util.DSObjectFactory;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.NoSubscriberEvent;

public class ChatMessageBrigdeService extends Service {

    private GetMessageThread messageThread;

    private NotificationManager notificationManager;

    private final Object lock = new Object();

    public static final String ACTION_GET_MESSAGE = "com.wanlonggroup.caiplus.bz.im.brigde_service_messages";

    public static final String ACTION_CANCEL_NOTIFY = "com.wanlonggroup.caiplus.bz.im.brigde_service_cancel_notify";

    public static void startService(Context context) {
        Intent intent = new Intent(context, ChatMessageBrigdeService.class);
        intent.setAction(ACTION_GET_MESSAGE);
        context.startService(intent);
    }

    public static void cancelNofity(Context context) {
        Intent intent = new Intent(context, ChatMessageBrigdeService.class);
        intent.setAction(ACTION_CANCEL_NOTIFY);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
            if (ACTION_GET_MESSAGE.equals(intent.getAction())) {
                startGetMsgThread();
            } else if (ACTION_CANCEL_NOTIFY.equals(intent.getAction())) {
                cancelNofiy();
            }
        }
        return START_STICKY;
    }

    public void onEvent(NoSubscriberEvent event) {
        if (event.originalEvent instanceof IMessaging) {
            EventBus.getDefault().postSticky(((IMessaging) event.originalEvent).toIMessage());
        } else {
            EventBus.getDefault().addEvent(event.originalEvent);
            if (event.originalEvent instanceof IMessage) {
                showNotification((IMessage) event.originalEvent);
            }
        }
    }

    void showNotification(IMessage message) {

        DSObject dsCy = new DSObject(CPModeName.CAIYOU_ITEM);
        dsCy.put("friendId", message.senderUserId);
        dsCy.put("friendName", message.senderUserName);
        dsCy.put("friendHeadIcon", message.senderHeadIcon);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setLargeIcon(
            BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher)).setSmallIcon(
            R.drawable.ic_launcher).setTicker("您有一条新的消息").setContentTitle(
            getResources().getString(R.string.app_name)).setContentText(
            message.senderUserName + "给您发了条消息").setAutoCancel(true).setOngoing(true);
        Notification notification = mBuilder.build();

        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;

        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://chat"));
        notificationIntent.putExtra("caiyou", dsCy);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(ChatMessageBrigdeService.this,
            message.senderUserId.hashCode(), notificationIntent, 0);
        notification.contentIntent = contentIntent;

        notificationManager.notify(1, notification);

    }

    void cancelNofiy() {
        notificationManager.cancel(1);
    }

    private void stopGetMsgThread() {
        if (messageThread != null) {
            ThreadExecutorsHelper.cancel(messageThread, true);
            messageThread = null;
        }
    }

    private void startGetMsgThread() {
        stopGetMsgThread();
        if (!TextUtils.isEmpty(SHEnvironment.getInstance().getLoginID())) {
            messageThread = new GetMessageThread();
            ThreadExecutorsHelper.execute(messageThread);
        }
    }

    class GetMessageThread implements Runnable {
        public void run() {
            synchronized (lock) {
                Log.i("GetMessageThread", "GetMessageThread");
                SHPostTaskM receiveMsgTask = new SHPostTaskM();
                receiveMsgTask.setUrl(ConfigSwitch.instance().wrapDomain(
                    BaseActivity.DEFAULT_API_URL + "miReceiveMessage.do"));
                receiveMsgTask.setListener(new ITaskListener() {

                    @Override
                    public void onTaskFinished(SHTask task) throws Exception {
                        DSObject dsChatMessages = DSObjectFactory.create(
                            CPModeName.CY_CHAT_MESSAGE_LIST).fromJson(task.getResult());
                        for (DSObject dsMsg : dsChatMessages.getArray(
                            CPModeName.CY_CHAT_MESSAGE_LIST, CPModeName.CY_CHAT_MESSAGE_ITEM)) {
                            Log.i("GetMessageThread", dsMsg.getString("senderuserid") + ">>>"
                                    + dsMsg.getString("chatcontent"));

                            IMessaging messaging = new IMessaging(dsMsg.getString("senderuserid"),
                                    dsMsg.getString("senderusername"),
                                    dsMsg.getString("senderheadicon"),
                                    dsMsg.getString("receiveruserid"),
                                    dsMsg.getString("chatcontent"), dsMsg.getString("sendtime"));
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

}
