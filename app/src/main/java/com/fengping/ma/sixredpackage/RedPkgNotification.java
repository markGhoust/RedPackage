package com.fengping.ma.sixredpackage;

import android.app.Notification;
import android.app.PendingIntent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class RedPkgNotification extends NotificationListenerService {
    final static String TAG = "fengping.ma.test";
    public RedPkgNotification() {
    }

    @Override
    public void onListenerConnected() {
        Log.i(TAG, "service start");
        super.onListenerConnected();
    }

    @Override
    public void onListenerDisconnected() {
        Log.i(TAG, "service end");
        super.onListenerDisconnected();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Notification notification = sbn.getNotification();
        String content = notification.extras.getCharSequence("android.text").toString();
        Log.i(TAG, "Notification content : " + content);
        if (content.contains("[微信红包]")) {
            PendingIntent intent = notification.contentIntent;
            try {
                intent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }
}
