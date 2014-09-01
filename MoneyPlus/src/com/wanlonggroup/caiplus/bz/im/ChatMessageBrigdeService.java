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

import com.next.util.Log;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.xdamon.app.DSObject;
import com.xdamon.executor.ThreadExecutorsHelper;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.NoSubscriberEvent;

public class ChatMessageBrigdeService extends Service {

    private NotificationManager notificationManager;

    private final Object lock = new Object();

    public static final String ACTION_MESSAGES = "com.wanlonggroup.caiplus.bz.im.brigde_service_messages";

    public static void sendMessage(Context context, DSObject dsMessages) {
        Intent intent = new Intent(context, ChatMessageBrigdeService.class);
        intent.putExtra("message", dsMessages);
        intent.setAction(ACTION_MESSAGES);
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
            if (ACTION_MESSAGES.equals(intent.getAction())) {
                DSObject messages = intent.getParcelableExtra("message");
                postMessage(messages);
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

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setTicker("您有一条新的消息")
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(message.senderUserName+"给您发了条消息")
                    .setAutoCancel(true)
                    .setOngoing(true);
        Notification notification = mBuilder.build();

        notification.defaults |= Notification.DEFAULT_SOUND;  
        notification.defaults |= Notification.DEFAULT_VIBRATE;  
        notification.defaults |= Notification.DEFAULT_LIGHTS; 

        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://chat"));
        notificationIntent.putExtra("caiyou", dsCy);
        PendingIntent contentIntent = PendingIntent.getActivity(ChatMessageBrigdeService.this, 0,
            notificationIntent, 0);
        notification.contentIntent = contentIntent;
        
        notificationManager.notify(1, notification);

    }

    void postMessage(final DSObject dsMsgArr) {

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                synchronized (lock) {
                    for (DSObject dsMsg : dsMsgArr.getArray(CPModeName.CY_CHAT_MESSAGE_LIST,
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
            }
        };
        ThreadExecutorsHelper.execute(runnable);
    }

}
