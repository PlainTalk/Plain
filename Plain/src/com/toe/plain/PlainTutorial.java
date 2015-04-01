package com.toe.plain;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class PlainTutorial extends SherlockFragmentActivity {

	PlainTutorialFragmentAdapter mAdapter;
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

	public void initPagerView(int position, View view) {
		FlipImageView flip = (FlipImageView) findViewById(R.id.ivXoxoTutorial);
		switch (position) {
		case 0:
			flip.setFlipped(true);
			break;
		case 1:
			flip.setFlipped(true);
			break;
		case 2:
			flip.setFlipped(true);
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
							Intent i = new Intent(getApplicationContext(),
									Welcome.class);
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
		PlainTutorialFragmentAdapter adapter = new PlainTutorialFragmentAdapter(
				PlainTutorial.this);

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