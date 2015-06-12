package com.toe.plain;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

class PlainFragmentAdapter extends PagerAdapter {

	protected static final String[] CONTENT = new String[] { "Plains",
			"Replies", "Tribes", "Favourites" };

	private int mCount = CONTENT.length;

	Plain activity;

	public PlainFragmentAdapter(Plain activity) {
		this.activity = activity;
	}

	public Object instantiateItem(View collection, int position) {
		LayoutInflater inflater = (LayoutInflater) collection.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		int resId = 0;
		switch (position) {
		case 0:
			resId = R.layout.plains_list_view;
			break;
		case 1:
			resId = R.layout.replies_list_view;
			break;
		case 2:
			resId = R.layout.tribes_list_view;
			break;
		case 3:
			resId = R.layout.list_view;
			break;
		}
		View view = inflater.inflate(resId, null);
		((ViewPager) collection).addView(view, 0);
		activity.initPagerView(position, view);
		return view;
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return PlainFragmentAdapter.CONTENT[position % CONTENT.length];
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