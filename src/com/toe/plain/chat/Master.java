package com.toe.plain.chat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class Master extends Service {

	@Override
	public void onCreate() {

		super.onCreate();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("service started", "started");
		Bundle bundle = intent.getExtras();
		ConnectionAsync async = new ConnectionAsync(this,
				bundle.getString("key1"), "key2");
		async.execute();

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		Intent restartServiceIntent = new Intent(getApplicationContext(),
				this.getClass());
		restartServiceIntent.setPackage(getPackageName());

		PendingIntent restartServicePendingIntent = PendingIntent.getService(
				getApplicationContext(), 1, restartServiceIntent,
				PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarmService = (AlarmManager) getApplicationContext()
				.getSystemService(Context.ALARM_SERVICE);
		alarmService.set(AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime() + 1000,
				restartServicePendingIntent);

		
	}

}
