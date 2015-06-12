package com.toe.plain;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class About extends SherlockActivity {

	Intent i;

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
		Typeface font = Typeface.createFromAsset(getAssets(),
				getString(R.string.font));

		RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);
		rippleBackground.startRippleAnimation();

		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTypeface(font);

		ShimmerTextView tvSlogan = (ShimmerTextView) findViewById(R.id.tvSlogan);
		new Shimmer().start(tvSlogan);
		tvSlogan.setTypeface(font);

		TextView tvToe = (TextView) findViewById(R.id.tvToe);
		tvToe.setTypeface(font);

		TextView tvAbout = (TextView) findViewById(R.id.tvAbout);
		tvAbout.setTypeface(font);

		TextView tvEasterEgg = (TextView) findViewById(R.id.tvEasterEgg);
		tvEasterEgg.setTypeface(font);
		tvEasterEgg
				.setOnLongClickListener(new AdapterView.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub\
						i = new Intent(getApplicationContext(), EasterEgg.class);
						startActivity(i);
						return false;
					}
				});
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
