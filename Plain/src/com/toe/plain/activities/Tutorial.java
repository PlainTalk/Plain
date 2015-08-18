package com.toe.plain.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.toe.plain.R;
import com.toe.plain.adapters.TutorialFragmentAdapter;
import com.toe.plain.classes.FlipImageView;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class Tutorial extends SherlockFragmentActivity {

	TutorialFragmentAdapter mAdapter;
	ViewPager mPager;
	PageIndicator mIndicator;
	Intent i;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.tutorial_view_pager);
		getSupportActionBar().hide();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

		setAdapter();
	}

	@SuppressWarnings("deprecation")
	public void initPagerView(int position, View view) {
		LinearLayout llTutorial = (LinearLayout) findViewById(R.id.llTutorial);
		TextView tvTutorial = (TextView) findViewById(R.id.tvTutorial);
		final int sdk = android.os.Build.VERSION.SDK_INT;

		switch (position) {
		case 0:
			if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				llTutorial.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.tutorial_one));
			} else {
				llTutorial.setBackground(getResources().getDrawable(
						R.drawable.tutorial_one));
			}

			tvTutorial.setText("Type out anything at all");
			break;
		case 1:
			if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				llTutorial.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.tutorial_two));
			} else {
				llTutorial.setBackground(getResources().getDrawable(
						R.drawable.tutorial_two));
			}

			tvTutorial.setText("Send it out into the world");
			break;
		case 2:
			if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				llTutorial.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.tutorial_three));
			} else {
				llTutorial.setBackground(getResources().getDrawable(
						R.drawable.tutorial_three));
			}

			tvTutorial.setText("Make tribes, create your own communities");
			break;
		case 3:
			if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				llTutorial.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.tutorial_four));
			} else {
				llTutorial.setBackground(getResources().getDrawable(
						R.drawable.tutorial_four));
			}

			tvTutorial.setText("Create relationships, make friends");
			break;
		case 4:
			final FlipImageView ivDone = (FlipImageView) findViewById(R.id.ivDone);
			ivDone.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ivDone.setFlipped(true);
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							SharedPreferences sp = getSharedPreferences(
									getPackageName(), MODE_PRIVATE);
							sp.edit().putBoolean("hasSeenTutorial", true)
									.commit();
							i = new Intent(getApplicationContext(),
									Preferences.class);
							startActivity(i);
						}
					}, 2000);
				}
			});
			break;
		}
	}

	private void setAdapter() {
		// TODO Auto-generated method stub
		TutorialFragmentAdapter adapter = new TutorialFragmentAdapter(
				Tutorial.this);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(adapter);
		mPager.setCurrentItem(0);
		mPager.setOffscreenPageLimit(0);

		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		finish();
	}
}