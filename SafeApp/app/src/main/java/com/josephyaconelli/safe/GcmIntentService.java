package com.josephyaconelli.safe;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by Joseph Yaconelli on 7/7/2016.
 */
public class GcmIntentService extends IntentService{

    public GcmIntentService(){
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if(!extras.isEmpty() && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){
            sendNotification("Received: " + extras.toString());
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    private void sendNotification(String msg){
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_plus_signin_btn_icon_dark_focused)
                        .setContentTitle("Safe App Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);
        mBuilder.setContentIntent(contentIntent);

        //TODO: figure out what that actually needs to be
        int NOTIFICATION_ID = 1;
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
