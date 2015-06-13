package com.toe.plain.chat;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ConnectionAsync extends AsyncTask<String, Void, String> {
	XmppConnection connector = new XmppConnection();
	Context ctx;
	String username;
	String password;

	public ConnectionAsync(Context context, String key1, String key2) {

		ctx = context;
		username = key1;
		password = key2;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String doInBackground(String... params) {

		connector.createConnection(ctx, username, password);
		return null;
	}

	@Override
	protected void onPostExecute(String result) {

		super.onPostExecute(result);

		Log.d("done connected", "connected");

	}

	@Override
	protected void onPreExecute() {

		super.onPreExecute();

	}

}