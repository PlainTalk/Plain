package com.toe.plain;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class Explore extends SherlockActivity {

	CategoriesListItemAdapter adapter;
	XListView listView;
	Intent intent;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		populateList();
	}

	private void populateList() {
		// TODO Auto-generated method stub
		ArrayList<CategoriesListItem> categories = new ArrayList<CategoriesListItem>();
		categories.add(new CategoriesListItem("Jokes", "Funny plains #funny"));
		categories.add(new CategoriesListItem("Relationships",
				"He's a boy and she's a girl #rship"));
		categories.add(new CategoriesListItem("Intimacy",
				"Plains about getting intimate #intimate"));
		categories
				.add(new CategoriesListItem("Life", "Plains about life #life"));
		categories.add(new CategoriesListItem("Love", "All about love #love"));
		categories.add(new CategoriesListItem("School",
				"What we're saying about school #sch"));
		categories
				.add(new CategoriesListItem("Work", "Workplace plains #work"));
		categories.add(new CategoriesListItem("Random",
				"People with random stuff to say #random"));
		categories.add(new CategoriesListItem("Late night",
				"Can't sleep? #latenyt"));
		categories.add(new CategoriesListItem("Morning",
				"Having an early morning? #morning"));
		categories.add(new CategoriesListItem("Short stories",
				"Short stories to kill time #story"));
		categories.add(new CategoriesListItem("Poems",
				"Poems by plainers #poem"));

		adapter = new CategoriesListItemAdapter(getApplicationContext(),
				R.layout.list_item, categories);
		listView = (XListView) findViewById(R.id.lvListItems);
		listView.setAdapter(adapter);
		listView.setPullLoadEnable(false);
		listView.setPullRefreshEnable(false);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				intent = new Intent(getApplicationContext(),
						ExploreSearchResults.class);
				Bundle b = new Bundle();
				b.putString(
						"hashtag",
						getResources().getStringArray(R.array.hashtags)[arg2 - 1]);
				intent.putExtras(b);
				startActivity(intent);
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

}