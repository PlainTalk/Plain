package com.toe.plain;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class About extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setUp();
	}

	private void setUp() {
		// TODO Auto-generated method stub
		Typeface font = Typeface.createFromAsset(getAssets(), getResources()
				.getString(R.string.font));

		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTypeface(font);

		TextView tvSlogan = (TextView) findViewById(R.id.tvSlogan);
		tvSlogan.setTypeface(font);

		TextView tvToe = (TextView) findViewById(R.id.tvToe);
		tvToe.setTypeface(font);

		TextView tvAbout= (TextView) findViewById(R.id.tvAbout);
		tvAbout.setTypeface(font);

		TextView tvEasterEgg = (TextView) findViewById(R.id.tvEasterEgg);
		tvEasterEgg.setTypeface(font);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
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
