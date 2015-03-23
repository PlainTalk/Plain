package com.toe.plain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

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
	String plainsMessage, repliesMessage, notificationMessage, lastId,
			lastReplyId;
	ArrayList<String> storedTags = new ArrayList<String>();
	ArrayList<String> jsonDocArray, jsonIdArray,
			fetchedTagStories = new ArrayList<String>();
	int i, numberOfReplies = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		setUp(context);
		getStoriesForReplies(context);
	}

	private void setUp(Context context) {
		// TODO Auto-generated method stub
		App42API.initialize(context, context.getString(R.string.api_key),
				context.getString(R.string.secret_key));
		storageService = App42API.buildStorageService();

		sp = context.getSharedPreferences(context.getPackageName(),
				Context.MODE_PRIVATE);
	}

	private void getStories(final Context context, final int numberOfReplies) {
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
						ArrayList<Storage.JSONDocument> jsonDocList = storage
								.getJsonDocList();
						jsonDocArray = new ArrayList<String>();
						jsonIdArray = new ArrayList<String>();

						for (int i = 0; i < jsonDocList.size(); i++) {
							jsonDocArray.add(jsonDocList.get(i).getJsonDoc());
							jsonIdArray.add(jsonDocList.get(i).getDocId());
						}

						processData(context, jsonIdArray, numberOfReplies);
					}

					public void onException(Exception ex) {
						System.out.println("Exception Message"
								+ ex.getMessage());
					}
				});
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> getTags() {
		// TODO Auto-generated method stub
		try {
			storedTags = (ArrayList<String>) ObjectSerializer
					.deserialize(sp.getString("storedTags",
							ObjectSerializer.serialize(new ArrayList<String>())));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return storedTags;
	}

	private void getStoriesForReplies(final Context context) {
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
						ArrayList<Storage.JSONDocument> jsonDocList = storage
								.getJsonDocList();
						jsonDocArray = new ArrayList<String>();
						jsonIdArray = new ArrayList<String>();

						for (int i = 0; i < jsonDocList.size(); i++) {
							jsonDocArray.add(jsonDocList.get(i).getJsonDoc());
							jsonIdArray.add(jsonDocList.get(i).getDocId());
						}

						fetchReplies(context, jsonDocArray, getTags(),
								jsonIdArray);
					}

					public void onException(Exception ex) {
						System.out.println("Exception Message"
								+ ex.getMessage());
					}
				});
	}

	private void fetchReplies(Context context, ArrayList<String> jsonDocArray,
			ArrayList<String> tags, ArrayList<String> jsonIdArray) {
		// TODO Auto-generated method stub
		lastReplyId = sp.getString("lastReplyId", null);
		sp.edit().putString("lastReplyId", jsonIdArray.get(0)).commit();

		for (int i = 0; i < jsonDocArray.size(); i++) {
			try {
				JSONObject json = new JSONObject(jsonDocArray.get(i));
				fetchedTagStories.add(json.getString("story"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (int i = 0; i < storedTags.size(); i++) {
			for (int j = 0; j < 100; j++) {
				if (fetchedTagStories.get(j).contains(
						"@" + storedTags.get(i).toLowerCase())) {
					numberOfReplies++;
				}
			}
		}

		getStories(context, numberOfReplies);
	}

	private void processData(final Context context,
			final ArrayList<String> jsonIdArray, int numberOfReplies) {
		// TODO Auto-generated method stub
		lastId = sp.getString("lastId", null);
		sp.edit().putString("lastId", jsonIdArray.get(0)).commit();

		if (lastId != null) {
			for (i = 1; i < jsonIdArray.size(); i++) {
				if (lastId.equals(jsonIdArray.get(i))) {
					break;
				}
			}

			if (i == jsonIdArray.size())
				plainsMessage = "How's your day?";
			else if (i == 1) {
				plainsMessage = "New plain,";
			} else {
				plainsMessage = i + " new plains,";
			}

			if (numberOfReplies == 0) {
				repliesMessage = ":-)";
			} else if (numberOfReplies == 1) {
				repliesMessage = "One reply!";
			} else {
				repliesMessage = numberOfReplies + " replies!";
			}

			notificationMessage = plainsMessage + " " + repliesMessage;

		} else {
			notificationMessage = "New plains and replies!";
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