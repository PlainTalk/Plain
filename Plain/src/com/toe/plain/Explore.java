package com.toe.plain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockActivity;

public class Explore extends SherlockActivity {

	private TagCloudView mTagCloudView;

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		getSupportActionBar().hide();

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();

		List<Tag> myTagList = createTags();
		mTagCloudView = new TagCloudView(this, width, height, myTagList);
		setContentView(mTagCloudView);
		mTagCloudView.requestFocus();
		mTagCloudView.setFocusableInTouchMode(true);
	}

	private List<Tag> createTags() {
		// create the list of tags with popularity values and related url
		List<Tag> tempList = new ArrayList<Tag>();

		String[] exploreWords = getResources().getStringArray(
				R.array.explore_words);

		for (int i = 0; i < exploreWords.length; i++) {
			tempList.add(new Tag(exploreWords[i],
					new Random().nextInt(10 - 2) + 2, exploreWords[i]));
		}
		return tempList;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		finish();
	}
}