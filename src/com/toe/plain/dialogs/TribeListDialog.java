package com.toe.plain.dialogs;

import java.util.ArrayList;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.toe.plain.R;
import com.toe.plain.adapters.StoredTribeListItemAdapter;
import com.toe.plain.classes.Shimmer;
import com.toe.plain.classes.ShimmerTextView;
import com.toe.plain.listitems.StoredTribeListItem;

public class TribeListDialog extends Dialog {

	private SherlockFragmentActivity activity;
	private ShimmerTextView tvNoListItem;
	public ArrayList<StoredTribeListItem> tribes;
	public ArrayList<String> savedHashtags;
	public ListView listView;
	private StoredTribeListItemAdapter adapter;

	public TribeListDialog(SherlockFragmentActivity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
		this.activity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tribe_list_dialog);

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
