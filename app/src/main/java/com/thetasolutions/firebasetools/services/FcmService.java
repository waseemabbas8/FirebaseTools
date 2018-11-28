package com.thetasolutions.firebasetools.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.thetasolutions.firebasetools.R;
import com.thetasolutions.firebasetools.activities.MainActivity;
import com.thetasolutions.firebasetools.models.MyNotification;
import com.thetasolutions.firebasetools.models.NotificationId;

public class FcmService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        Log.d("Token", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("MessageFrom", "From: " + remoteMessage.getFrom());

        MyNotification myNotification=new MyNotification();
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("MessageData", "Message data payload: " + remoteMessage.getData());
            //parsing the data from the incomming json
            myNotification.setTitle(remoteMessage.getData().get("title"));
            myNotification.setBody(remoteMessage.getData().get("body"));
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("body", "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String clickAction=remoteMessage.getNotification().getClickAction();
            sendNotification(myNotification,clickAction);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(MyNotification notificationModel,String clickAction) {
        Intent intent;
        //checking which activity is sent to be open in notiification
        if (clickAction.equals("NotificationActivity")) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("NotificationObject", notificationModel);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }else {
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), NotificationId.getID() /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notificationModel.getTitle())
                        .setContentText(notificationModel.getBody())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(NotificationId.getID() /* ID of notification */, notificationBuilder.build());
    }
}
