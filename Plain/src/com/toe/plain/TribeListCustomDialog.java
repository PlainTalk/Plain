package com.toe.plain;

import java.util.ArrayList;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

public class TribeListCustomDialog extends Dialog {

	private SherlockFragmentActivity activity;
	private ShimmerTextView tvNoListItem;
	public ArrayList<TribeListItem> tribes;
	public ArrayList<String> savedHashtags;
	public ListView listView;
	private TribeListItemAdapter adapter;

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

		tvNoListItem = (ShimmerTextView) findViewById(R.id.tvNoListItem);
		tvNoListItem.setVisibility(View.INVISIBLE);
		tribes = new ArrayList<TribeListItem>();

		if (savedHashtags.size() > 0) {
			for (int i = 0; i < savedHashtags.size(); i++) {
				tribes.add(new TribeListItem(savedHashtags.get(i)));
			}
		} else {
			tvNoListItem.setVisibility(View.VISIBLE);
			tvNoListItem.setText("Start with #tribes");
			new Shimmer().start(tvNoListItem);
		}

		listView = (ListView) findViewById(R.id.lvListItems);
		activity.runOnUiThread(new Runnable() {
			public void run() {
				adapter = new TribeListItemAdapter(activity
						.getApplicationContext(), R.layout.list_item, tribes);
				SwingBottomInAnimationAdapter swing = new SwingBottomInAnimationAdapter(
						adapter);
				swing.setAbsListView(listView);
				listView.setAdapter(swing);

			}
		});
	}
}
