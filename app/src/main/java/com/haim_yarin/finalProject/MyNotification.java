package com.haim_yarin.finalProject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;



public class MyNotification {
    private NotificationManager notificationManager;
    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "Channel 1 Demo";
    private static int notificationId = 1;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean firstRun = true;
    private ItemsList itemsList;
    private Authentication auth;


    //manage the natification
    public MyNotification(NotificationManager notificationManager, Context context, ItemsList itemsList,Authentication auth){

        this.auth = auth;
        this.itemsList = itemsList;
        this.context = context;
        // 1. Get reference to Notification Manager
        this.notificationManager = notificationManager;

        // 2. Create Notification Channel ONLY ONEs.
        //    Need for Android 8.0 (API level 26) and higher.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            //Create channel only if it is not already created
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null)
            {
                NotificationChannel notificationChannel = new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT); // NotificationManager.IMPORTANCE_HIGH

                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

    }

    public void ListenToChange(){
        db.collection("items")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("error", "Listen failed.", e);
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    JSONObject Jitem = new JSONObject(dc.getDocument().getData());
                                    Item item = null;
                                    try {
                                        item = new Item(Jitem);
                                        itemsList.addItem(item);
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                    if(!firstRun){
                                            if(auth.getUser() == null || auth.getUser().getUid().compareTo(item.getUser().getUid())  != 0)
                                            showNotification("new item", item.getTitle());

                                    }
                                    break;
                                case MODIFIED: // not in use

                                    break;
                                case REMOVED: // not in use

                                    break;
                            }
                        }
                        firstRun = false;
                    }
                });

    }


    public void showNotification(String notificationTitle, String notificationText)
    {
        Intent intent = new Intent(this.context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.context,0,intent,0);

        // Build Notification with NotificationCompat.Builder
        // on Build.VERSION < Oreo the notification avoid the CHANEL_ID
        Notification notification = new NotificationCompat.Builder(this.context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)  //Set the icon
                .setContentTitle(notificationTitle)         //Set the title of notification
                .setContentText(notificationText)           //Set the text for notification
                .setContentIntent(pendingIntent)            // Starts Intent when notification clicked
                //.setOngoing(true)                         // stick notification
                .setAutoCancel(true)                        // close notification when clicked
                .build();

        // Send the notification to the device Status bar.
        notificationManager.notify(notificationId, notification);

        notificationId++;  // for multiple(grouping) notifications on the same chanel
    }
}
