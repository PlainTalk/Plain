package com.toe.plain;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;

public class NotificationReceiver extends BroadcastReceiver {

	StorageService storageService;
	SharedPreferences sp;
	String notificationMessage, lastId;
	int i;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		setUp(context);
		getData(context);
	}

	private void setUp(Context context) {
		// TODO Auto-generated method stub
		App42API.initialize(context, context.getString(R.string.api_key),
				context.getString(R.string.secret_key));
		storageService = App42API.buildStorageService();
	}

	private void getData(final Context context) {
		// TODO Auto-generated method stub
		HashMap<String, String> metaHeaders = new HashMap<String, String>();
		metaHeaders.put("orderByDescending", "_$createdAt");
		storageService.setOtherMetaHeaders(metaHeaders);
		storageService.findAllDocuments(
				context.getString(R.string.database_name),
				context.getString(R.string.collection_name),
				new App42CallBack() {
					public void onSuccess(Object response) {
						Storage storage = (Storage) response;
						System.out.println("dbName is " + storage.getDbName());
						System.out.println("collection Name is "
								+ storage.getCollectionName());
						ArrayList<Storage.JSONDocument> jsonDocList = storage
								.getJsonDocList();
						ArrayList<String> jsonDocArray = new ArrayList<String>();
						ArrayList<String> jsonIDArray = new ArrayList<String>();

						for (int i = 0; i < jsonDocList.size(); i++) {
							System.out.println("objectId is "
									+ jsonDocList.get(i).getDocId());
							System.out.println("CreatedAt is "
									+ jsonDocList.get(i).getCreatedAt());
							System.out.println("UpdatedAtis "
									+ jsonDocList.get(i).getUpdatedAt());
							System.out.println("Jsondoc is "
									+ jsonDocList.get(i).getJsonDoc());

							jsonDocArray.add(jsonDocList.get(i).getJsonDoc());
							jsonIDArray.add(jsonDocList.get(i).getDocId());
						}

						processData(context, jsonIDArray);
					}

					public void onException(Exception ex) {
						System.out.println("Exception Message"
								+ ex.getMessage());
					}
				});
	}

	private void processData(final Context context,
			final ArrayList<String> jsonIDArray) {
		// TODO Auto-generated method stub
		sp = context.getSharedPreferences(context.getPackageName(),
				Context.MODE_PRIVATE);

		lastId = sp.getString("lastId", null);
		sp.edit().putString("lastId", jsonIDArray.get(0)).commit();

		if (lastId != null) {
			for (i = 1; i < jsonIDArray.size(); i++) {
				if (lastId.equals(jsonIDArray.get(i))) {
					break;
				}
			}

			if (i == jsonIDArray.size())
				notificationMessage = "New stories!";
			else if (i > 1) {
				notificationMessage = "New story!";
			} else {
				notificationMessage = i + " new stories!";
			}

		} else {
			notificationMessage = "New stories!";
		}

		showNotification(context, notificationMessage);
	}

	private void showNotification(Context context, String notificationMessage) {
		// TODO Auto-generated method stub
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent notificationIntent;
		try {
			notificationIntent = new Intent(context, Plain.class);
			notificationIntent.putExtra("message_delivered", true);
			notificationIntent.putExtra("message", notificationMessage);

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
				.setSmallIcon(R.drawable.plain_icon_60)
				.setWhen(System.currentTimeMillis()).setAutoCancel(true)
				.build();

		notification.contentIntent = intent;
		notification.defaults |= Notification.FLAG_NO_CLEAR;
		notificationManager.notify(0, notification);
	}
}