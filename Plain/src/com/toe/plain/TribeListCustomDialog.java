package com.toe.plain;

import java.util.ArrayList;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

public class TribeListCustomDialog extends Dialog {

	private SherlockFragmentActivity activity;
	private ShimmerTextView tvNoListItem;
	public ArrayList<StoredTribeListItem> tribes;
	public ArrayList<String> savedHashtags;
	public ListView listView;
	private StoredTribeListItemAdapter adapter;

	public TribeListCustomDialog(SherlockFragmentActivity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
		this.activity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tribe_list_custom_dialog);

		Typeface font = Typeface.createFromAsset(activity.getAssets(),
				activity.getString(R.string.font));

		tvNoListItem = (ShimmerTextView) findViewById(R.id.tvNoListItem);
		tvNoListItem.setVisibility(View.INVISIBLE);
		tvNoListItem.setTypeface(font);
		tribes = new ArrayList<StoredTribeListItem>();

		if (savedHashtags.size() > 0) {
			for (int i = 0; i < savedHashtags.size(); i++) {
				tribes.add(new StoredTribeListItem(savedHashtags.get(i)));
			}
		} else {
			tvNoListItem.setVisibility(View.VISIBLE);
			tvNoListItem.setText("Start with #tribes");
			new Shimmer().start(tvNoListItem);
		}

		listView = (ListView) findViewById(R.id.lvListItems);
		activity.runOnUiThread(new Runnable() {
			public void run() {
				adapter = new StoredTribeListItemAdapter(activity
						.getApplicationContext(),
						R.layout.stored_tribe_list_item, tribes);
				SwingBottomInAnimationAdapter swing = new SwingBottomInAnimationAdapter(
						adapter);
				swing.setAbsListView(listView);
				listView.setAdapter(swing);

			}
		});
	}
}
