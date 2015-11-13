package com.toe.plain.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class StartReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent arg1) {

		SharedPreferences sp = context.getSharedPreferences(
				context.getPackageName(), Context.MODE_PRIVATE);
		
		Bundle bundle = new Bundle();
		bundle.putString("key1", sp.getString("screenName", null));
		bundle.putString("key2", sp.getString("password", null));
		Intent intent = new Intent(context, Master.class);
		intent.putExtras(bundle);
		context.startService(intent);
		Log.i("Autostart", "started");
	}
}

