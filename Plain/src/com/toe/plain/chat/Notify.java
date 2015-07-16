package com.toe.plain.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.toe.plain.R;
import com.toe.plain.activities.Chat;
import com.toe.plain.activities.Plain;

public class Notify {

	public static void showNotification(Context context,
			String notificationMessage, String tag, String thread) {
		// TODO Auto-generated method stub
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent notificationIntent;
		try {
			notificationIntent = new Intent(context, Chat.class);
			notificationIntent.putExtra("tag", tag);
			notificationIntent.putExtra("receiverScreenName", thread);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			notificationIntent = new Intent(context, Plain.class);

		}

		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		Notification notification = new NotificationCompat.Builder(context)
				.setContentTitle("'Plain").setContentText(notificationMessage)
				.setSmallIcon(R.drawable.ic_launcher)
				.setWhen(System.currentTimeMillis()).setAutoCancel(true)
				.build();

		notification.contentIntent = intent;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);
	}
}
