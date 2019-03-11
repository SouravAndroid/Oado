package com.oado.services;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.oado.R;
import com.oado.activity.Splash;
import com.oado.database.DatabaseHelper;
import com.oado.utils.Constants;
import com.oado.utils.PrefManager;

import java.util.List;

/**
 * Created by Developer on 1/23/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private PrefManager prefManager;
    private DatabaseHelper databaseHelper;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "remoteMessage: " + remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "data: " + remoteMessage.getData());
        Log.d(TAG, "title: " + remoteMessage.getData().get("title"));
        Log.d(TAG, "body: " + remoteMessage.getData().get("body"));
        Log.d(TAG, "notification_type: " + remoteMessage.getData().get("noti_type"));
        Log.d(TAG, "message_type: " + remoteMessage.getData().get("msg_type"));

        prefManager = new PrefManager(getApplicationContext());


        if (prefManager.isLogin()){

            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            String notification_type = remoteMessage.getData().get("noti_type"); // 1 to 5
            String message_type = remoteMessage.getData().get("msg_type"); // M / D


            databaseHelper = new DatabaseHelper(getApplicationContext());
            databaseHelper.insertData(body, "");


            if (message_type != null){

                if (message_type.equalsIgnoreCase("M")){

                    int c = prefManager.getKey6();
                    prefManager.setKeyCounter6(c+1);

                    countResponse(getApplicationContext(), 6);

                    NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                    notificationHelper.createNotification(title, body);


                }else if (message_type.equalsIgnoreCase("D")){

                    setCount(Integer.parseInt(notification_type));

                    NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                    notificationHelper.createNotification(title, body);


                }else if (message_type.equalsIgnoreCase("IO")){

                    NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                    notificationHelper.createNotificationForNotiFrag(title, body);

                }

            }

        }


    }


    private void setCount(int type){
        int c = 0;

        switch (type){

            case 1:

                c = prefManager.getKey1();
                prefManager.setKeyCounter1(c+1);

                break;

            case 2:

                c = prefManager.getKey2();
                prefManager.setKeyCounter2(c+1);

                break;

            case 3:

                c = prefManager.getKey3();
                prefManager.setKeyCounter3(c+1);

                break;

            case 4:

                c = prefManager.getKey4();
                prefManager.setKeyCounter4(c+1);

                break;

            case 5:

                c = prefManager.getKey5();
                prefManager.setKeyCounter5(c+1);

                break;

        }


        countResponse(getApplicationContext(), type);

    }

    static void countResponse(Context context, int type_) {
        Intent intent = new Intent(Constants.message_coming_count);
        //put whatever data you want to send, if any
        intent.putExtra("type", type_);
        //send broadcast
        context.sendBroadcast(intent);
    }




















    //////////// use less ..........




    private void sendNotification(String messageTitle, String messageBody) {

        Intent intent = new Intent(this, Splash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0 , intent, PendingIntent.FLAG_ONE_SHOT);
        long[] pattern = {400};
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(this)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setSound(defaultSoundUri)
               // .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          //  notificationManager.notify(getNotId(), notificationBuilder.build());

            CharSequence name = "Oado";
            String description = "oado";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager1 = getSystemService(NotificationManager.class);
            notificationManager1.createNotificationChannel(channel);

        } else {
            notificationManager.notify(getNotId(), notificationBuilder.build());
        }





    }


    private void sendNotification2(String messageTitle, String messageBody) {
        Intent intent = new Intent(this, Splash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0 /* request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long[] pattern = {400};
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(this)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setLights(Color.YELLOW, 1, 1)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            notificationManager.notify(getNotId(), notificationBuilder.build());
        } else {
            notificationManager.notify(getNotId(), notificationBuilder.build());
        }

    }

    public void sendNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.oado_launcher);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.journaldev.com/"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.oado_launcher));
        builder.setContentTitle("Notifications Title");
        builder.setContentText("Your notification content here.");
        builder.setSubText("Tap to view the website.");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        notificationManager.notify(1, builder.build());
    }

    private int getNotId(){
        int id = (int) System.currentTimeMillis();
        return id;
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.oado_launcher : R.mipmap.oado_launcher;
    }

    private boolean applicationInForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> services = activityManager.getRunningAppProcesses();
        boolean isActivityFound = false;

        if (services.get(0).processName.equalsIgnoreCase(getPackageName())) {
            isActivityFound = true;
        }

        return isActivityFound;
    }


    private String getForegroundActivity(){
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        //Log.d("TAG", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        componentInfo.getPackageName();

        return taskInfo.get(0).topActivity.getClassName();
    }





}



