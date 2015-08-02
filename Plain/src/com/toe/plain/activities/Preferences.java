package com.toe.plain.activities;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.toe.plain.R;

public class Preferences extends SherlockActivity {

	String error;
	SharedPreferences sp;
	SherlockActivity activity;
	Intent i;
	private SecureRandom random = new SecureRandom();
	Spinner spCountry;

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

		Collections.sort(countries);
		countries.add(0, "Select your country");

		spCountry = (Spinner) findViewById(R.id.spCountry);
		ArrayAdapter<String> nationalityAdapter = new ArrayAdapter<String>(
				getApplicationContext(), R.layout.simple_spinner_item,
				countries);
		nationalityAdapter
				.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		spCountry.setAdapter(nationalityAdapter);
		spCountry.setSelection(sp.getInt("countryPosition", 0));
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
			String country = spCountry.getSelectedItem().toString().trim()
					.replaceAll("([^a-zA-Z]|\\s)+", " ").replaceAll(" ", "")
					.toUpperCase();

			if (!country.contains("SELECT")) {
				sp.edit().putString("username", getRandomString()).commit();
				sp.edit().putString("password", getRandomString()).commit();
				sp.edit().putString("country", country).commit();
				sp.edit()
						.putInt("countryPosition",
								spCountry.getSelectedItemPosition()).commit();
				sp.edit().putBoolean("registered", true).commit();
				Toast.makeText(getApplicationContext(), "Done",
						Toast.LENGTH_SHORT).show();
				i = new Intent(getApplicationContext(), Splash.class);
				startActivity(i);
			} else {
				Toast.makeText(getApplicationContext(),
						"Please select your country", Toast.LENGTH_SHORT)
						.show();
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
