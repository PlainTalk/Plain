package com.toe.plain;

import java.math.BigInteger;
import java.security.SecureRandom;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class SignUp extends SherlockActivity {

	EditText etUsername;
	String organisationCode, username, emailAddress, error;
	SharedPreferences sp;
	SherlockActivity activity;
	Intent i;
	private SecureRandom random = new SecureRandom();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up);
		getSupportActionBar().setHomeButtonEnabled(true);

		setUp();
	}

	private void setUp() {
		// TODO Auto-generated method stub
		sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
		etUsername = (EditText) findViewById(R.id.etUsername);
	}

	public String getRandomString() {
		return new BigInteger(130, random).toString(32);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.signup_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.mDone:
			String username = etUsername.getText().toString().trim();
			if (username.length() > 2) {
				sp.edit().putString("ID1", getRandomString()).commit();
				sp.edit().putString("ID2", getRandomString()).commit();
				sp.edit().putString("username", username).commit();
				Toast.makeText(getApplicationContext(), "Done",
						Toast.LENGTH_SHORT).show();
				i = new Intent(getApplicationContext(), Welcome.class);
				startActivity(i);
			} else {
				Toast.makeText(getApplicationContext(),
						"Please enter a username with at least 3 characters",
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

}
