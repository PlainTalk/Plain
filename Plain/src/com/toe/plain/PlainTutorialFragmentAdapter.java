package com.toe.plain;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

class PlainTutorialFragmentAdapter extends PagerAdapter {

	PlainTutorial activity;

	public PlainTutorialFragmentAdapter(PlainTutorial activity) {
		this.activity = activity;
	}

	public Object instantiateItem(View collection, int position) {
		LayoutInflater inflater = (LayoutInflater) collection.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		int resId = 0;
		switch (position) {
		case 0:
			resId = R.layout.plain_tutorial_one;
			break;
		case 1:
			resId = R.layout.plain_tutorial_two;
			break;
		case 2:
			resId = R.layout.plain_tutorial_three;
			break;
		}
		View view = inflater.inflate(resId, null);
		((ViewPager) collection).addView(view, 0);
		activity.initPagerView(position, view);
		return view;
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView((View) arg2);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == ((View) arg1);
	}

	@Override
	public Parcelable saveState() {
		return null;
	}
}