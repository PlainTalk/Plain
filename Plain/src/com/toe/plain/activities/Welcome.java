package com.toe.plain.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.toe.plain.R;
import com.toe.plain.classes.RippleBackground;
import com.toe.plain.classes.Shimmer;
import com.toe.plain.classes.ShimmerTextView;

public class Welcome extends SherlockActivity {

	Intent i;
	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		getSupportActionBar().hide();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

		setUp();
	}

	private void setUp() {
		// TODO Auto-generated method stub
		sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
		if (sp.getBoolean("hasSeenTutorial", false) == false) {
			i = new Intent(getApplicationContext(), PlainTutorial.class);
			startActivity(i);
		} else if (sp.getBoolean("registered", false) == false) {
			i = new Intent(getApplicationContext(), SignUp.class);
			startActivity(i);
		} else if (sp.getBoolean("firstTime", true) == true) {
			// Do nothing
		} else {
			i = new Intent(getApplicationContext(), Plain.class);
			startActivity(i);
		}

		Typeface font = Typeface.createFromAsset(getAssets(),
				getString(R.string.font));

		RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);
		rippleBackground.startRippleAnimation();

		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTypeface(font);

		ShimmerTextView tvSlogan = (ShimmerTextView) findViewById(R.id.tvSlogan);
		new Shimmer().start(tvSlogan);
		tvSlogan.setTypeface(font);

		Button bContinue = (Button) findViewById(R.id.bContinue);
		bContinue.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sp.edit().putBoolean("firstTime", false).commit();
				i = new Intent(getApplicationContext(), Plain.class);
				startActivity(i);
			}
		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		finish();
	}
}
