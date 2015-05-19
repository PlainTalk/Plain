package com.toe.plain;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
	AutoCompleteTextView actCountry;

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

		String[] countryArray = getResources()
				.getStringArray(R.array.countries);
		final ArrayList<String> countries = new ArrayList<String>();
		final ArrayList<String> countryCodes = new ArrayList<String>();

		for (int i = 1; i < countryArray.length; i++) {
			String[] countryArraySplit = countryArray[i].split(";");
			String country = countryArraySplit[0];
			String countryCode = countryArraySplit[2];
			countries.add(country);
			countryCodes.add("+" + countryCode + "-");
		}

		actCountry = (AutoCompleteTextView) findViewById(R.id.actCountry);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.spinner_dropdown_item, countries);
		actCountry.setAdapter(adapter);
		actCountry.setHintTextColor(Color.WHITE);

		etUsername = (EditText) findViewById(R.id.etUsername);
		etUsername.setHintTextColor(Color.WHITE);

		actCountry.setText(sp.getString("country", ""));
		etUsername.setText(sp.getString("screenName", ""));
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
			String country = actCountry.getText().toString().trim()
					.replaceAll("([^a-zA-Z]|\\s)+", " ").toUpperCase();
			if (country.length() > 2) {
				if (username.length() > 2) {
					sp.edit().putString("username", getRandomString()).commit();
					sp.edit().putString("password", getRandomString()).commit();
					sp.edit().putString("screenName", username).commit();
					sp.edit().putString("country", country).commit();
					sp.edit().putBoolean("registered", true).commit();
					Toast.makeText(getApplicationContext(), "Done",
							Toast.LENGTH_SHORT).show();
					i = new Intent(getApplicationContext(), Welcome.class);
					startActivity(i);
				} else {
					etUsername
							.setError("Please enter a username with at least 3 characters");
				}
			} else {
				actCountry.setError("Please enter your country");

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
