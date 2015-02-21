package com.toe.plain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CacheManager;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42CacheManager.Policy;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;
import com.toe.plain.PullToRefreshListView.OnRefreshListener;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class Plain extends SherlockFragmentActivity {

	ArrayList<ListItem> stories, likes, tags, admins;
	PlainFragmentAdapter mAdapter;
	ViewPager mPager;
	Intent i;
	PageIndicator mIndicator;
	ListItemAdapter adapter;
	PullToRefreshListView listView;
	StorageService storageService;
	String error;
	EditText etStory;
	SherlockFragmentActivity activity;
	ShimmerTextView tvNoListItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.view_pager);
		getSupportActionBar().setHomeButtonEnabled(true);
		setSupportProgressBarIndeterminateVisibility(false);
		setAdapter();

		setUp();
	}

	private void setUp() {
		// TODO Auto-generated method stub
		activity = this;

		App42API.initialize(getApplicationContext(),
				getString(R.string.api_key), getString(R.string.secret_key));
		App42CacheManager.setPolicy(Policy.NETWORK_FIRST);
		storageService = App42API.buildStorageService();
	}

	public void initPagerView(int position, final View view) {

		switch (position) {
		case 0:
			ImageView ivBackground = (ImageView) findViewById(R.id.ivBackground);

			final float growTo = 1.2f;
			final float growFrom = 1.0f;
			final float shrinkTo = 0.83f;
			final float shrinkFrom = 1.2f;
			final long duration = 40000;

			ScaleAnimation grow = new ScaleAnimation(growFrom, growTo,
					growFrom, growTo, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			grow.setDuration(duration / 2);
			ScaleAnimation shrink = new ScaleAnimation(shrinkFrom, shrinkTo,
					shrinkFrom, shrinkTo, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			shrink.setDuration(duration / 2);
			shrink.setStartOffset(duration / 2);
			AnimationSet growAndShrink = new AnimationSet(true);
			growAndShrink.setInterpolator(new LinearInterpolator());
			growAndShrink.addAnimation(grow);
			growAndShrink.addAnimation(shrink);
			ivBackground.startAnimation(growAndShrink);

			etStory = (EditText) findViewById(R.id.etStory);
			final FlipImageView ivDone = (FlipImageView) findViewById(R.id.ivDone);
			ivDone.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ivDone.setFlipped(true);
					etStory.setEnabled(false);
					String story = etStory.getText().toString().trim();
					publishStory(story);
				}
			});
			break;
		case 1:
			Typeface font = Typeface.createFromAsset(getAssets(),
					getString(R.string.font));

			tvNoListItem = (ShimmerTextView) findViewById(R.id.tvNoListItem);
			new Shimmer().start(tvNoListItem);
			tvNoListItem.setTypeface(font);

			getData();
			listView = (PullToRefreshListView) findViewById(R.id.lvListItems);
			listView.setOnRefreshListener(new OnRefreshListener() {

				@Override
				public void onRefresh() {
					// TODO Auto-generated method stub
					getData();
				}
			});
			break;
		}
	}

	private void getData() {
		// TODO Auto-generated method stub
		setSupportProgressBarIndeterminateVisibility(true);
		HashMap<String, String> metaHeaders = new HashMap<String, String>();
		metaHeaders.put("orderByAscending", "_$createdAt");
		storageService.setOtherMetaHeaders(metaHeaders);
		storageService.findAllDocuments(getString(R.string.database_name),
				getString(R.string.collection_name), new App42CallBack() {
					public void onSuccess(Object response) {
						Storage storage = (Storage) response;
						System.out.println("dbName is " + storage.getDbName());
						System.out.println("collection Name is "
								+ storage.getCollectionName());
						ArrayList<Storage.JSONDocument> jsonDocList = storage
								.getJsonDocList();
						ArrayList<String> jsonDocArray = new ArrayList<String>();
						ArrayList<String> jsonIDArray = new ArrayList<String>();

						for (int i = 0; i < jsonDocList.size(); i++) {
							System.out.println("objectId is "
									+ jsonDocList.get(i).getDocId());
							System.out.println("CreatedAt is "
									+ jsonDocList.get(i).getCreatedAt());
							System.out.println("UpdatedAtis "
									+ jsonDocList.get(i).getUpdatedAt());
							System.out.println("Jsondoc is "
									+ jsonDocList.get(i).getJsonDoc());

							jsonDocArray.add(jsonDocList.get(i).getJsonDoc());
							jsonIDArray.add(jsonDocList.get(i).getDocId());
						}

						populateList(jsonDocArray, jsonIDArray);
					}

					public void onException(Exception ex) {
						System.out.println("Exception Message"
								+ ex.getMessage());
						errorHandler(ex);
					}
				});
	}

	private void publishStory(String story) {
		// TODO Auto-generated method stub
		setSupportProgressBarIndeterminateVisibility(true);
		Toast.makeText(getApplicationContext(), "Just a moment",
				Toast.LENGTH_SHORT).show();

		JSONObject jsonStory = new JSONObject();
		try {
			jsonStory.put("story", story);
			jsonStory.put("likes", 0);
			jsonStory.put("tag", RandomStringUtils.random(3, true, true));
			jsonStory.put("admin", false);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		storageService.insertJSONDocument(getString(R.string.database_name),
				getString(R.string.collection_name), jsonStory,
				new App42CallBack() {
					public void onSuccess(Object response) {
						Storage storage = (Storage) response;
						System.out.println("dbName is " + storage.getDbName());
						System.out.println("collection Name is "
								+ storage.getCollectionName());
						final ArrayList<Storage.JSONDocument> jsonDocList = storage
								.getJsonDocList();

						for (int i = 0; i < jsonDocList.size(); i++) {
							System.out.println("objectId is "
									+ jsonDocList.get(i).getDocId());
							System.out.println("CreatedAt is "
									+ jsonDocList.get(i).getCreatedAt());
							System.out.println("UpdatedAtis "
									+ jsonDocList.get(i).getUpdatedAt());
							System.out.println("Jsondoc is "
									+ jsonDocList.get(i).getJsonDoc());
						}

						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								etStory.setEnabled(true);
								etStory.setText("");
								setSupportProgressBarIndeterminateVisibility(false);
								Toast.makeText(getApplicationContext(),
										"Story published!", Toast.LENGTH_SHORT)
										.show();
								getData();
							}
						});
					}

					public void onException(Exception ex) {
						System.out.println("Exception Message"
								+ ex.getMessage());
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								setSupportProgressBarIndeterminateVisibility(false);
							}
						});
						errorHandler(ex);
					}
				});
	}

	private void populateList(final ArrayList<String> jsonDocArray,
			final ArrayList<String> jsonIDArray) {
		// TODO Auto-generated method stub
		Collections.reverse(jsonDocArray);
		Collections.reverse(jsonIDArray);

		stories = new ArrayList<ListItem>();

		for (int i = 0; i < jsonDocArray.size(); i++) {
			try {
				JSONObject json = new JSONObject(jsonDocArray.get(i));
				stories.add(new ListItem(json.getString("story"), json
						.getInt("likes"), json.getString("tag"), json
						.getBoolean("admin")));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				tvNoListItem.setVisibility(View.INVISIBLE);
				setSupportProgressBarIndeterminateVisibility(false);
				adapter = new ListItemAdapter(getApplicationContext(),
						R.layout.list_item, stories);
				SwingBottomInAnimationAdapter swing = new SwingBottomInAnimationAdapter(
						adapter);
				swing.setAbsListView(listView);
				listView.setAdapter(swing);
				listView.onRefreshComplete();
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						try {
							addLike(jsonIDArray.get(arg2 - 1),
									new JSONObject(jsonDocArray.get(arg2 - 1))
											.getInt("likes"), new JSONObject(
											jsonDocArray.get(arg2 - 1))
											.getString("story"),
									new JSONObject(jsonDocArray.get(arg2 - 1))
											.getString("tag"), new JSONObject(
											jsonDocArray.get(arg2 - 1))
											.getBoolean("admin"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						// TODO Auto-generated method stub
						i = new Intent(android.content.Intent.ACTION_SEND);
						i.setType("text/plain");
						try {
							i.putExtra(
									android.content.Intent.EXTRA_TEXT,
									"\""
											+ new JSONObject(jsonDocArray
													.get(arg2 - 1))
													.getString("plain")
											+ "\"\n\n-story from 'Plain");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						startActivity(Intent.createChooser(i,
								"Share the story using..."));
						return false;
					}
				});
			}
		});
	}

	private void addLike(String jsonDocId, int currentLikes, String story,
			String tag, boolean admin) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "Liking...", Toast.LENGTH_SHORT)
				.show();
		setSupportProgressBarIndeterminateVisibility(false);

		JSONObject likedStory = new JSONObject();
		try {
			likedStory.put("story", story);
			likedStory.put("likes", currentLikes + 1);
			likedStory.put("tag", tag);
			likedStory.put("admin", admin);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		storageService.updateDocumentByDocId(getString(R.string.database_name),
				getString(R.string.collection_name), jsonDocId, likedStory,
				new App42CallBack() {
					public void onSuccess(Object response) {
						Storage storage = (Storage) response;
						System.out.println("dbName is " + storage.getDbName());
						System.out.println("collection Name is "
								+ storage.getCollectionName());
						ArrayList<Storage.JSONDocument> jsonDocList = storage
								.getJsonDocList();
						for (int i = 0; i < jsonDocList.size(); i++) {
							System.out.println("objectId is "
									+ jsonDocList.get(i).getDocId());
							System.out.println("CreatedAt is "
									+ jsonDocList.get(i).getCreatedAt());
							System.out.println("UpdatedAtis "
									+ jsonDocList.get(i).getUpdatedAt());
							System.out.println("Jsondoc is "
									+ jsonDocList.get(i).getJsonDoc());
						}

						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								setSupportProgressBarIndeterminateVisibility(false);
								Toast.makeText(getApplicationContext(),
										"Liked!", Toast.LENGTH_SHORT).show();
								getData();
							}
						});
					}

					public void onException(Exception ex) {
						System.out.println("Exception Message"
								+ ex.getMessage());
						errorHandler(ex);
					}
				});

	}

	private void errorHandler(Exception ex) {
		// TODO Auto-generated method stub
		if (ex.getMessage().contains("refused")
				|| ex.getMessage().contains("UnknownHostException")
				|| ex.getMessage().contains("SSL")
				|| ex.getMessage().contains("ConnectTimeoutException")
				|| ex.getMessage().contains("Neither")
				|| ex.getMessage().contains("Socket")) {
			error = "No internet connection :-(";

			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					tvNoListItem.setVisibility(View.VISIBLE);
					tvNoListItem.setText("Refresh");
					tvNoListItem.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							getData();
						}
					});
				}
			});
		} else if (ex.getMessage().contains("No document")) {
			error = "No stories to read yet :-(";
		} else {
			error = ex.getMessage();
		}

		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				setSupportProgressBarIndeterminateVisibility(false);
				Toast.makeText(getApplicationContext(), error,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void setAdapter() {
		// TODO Auto-generated method stub
		PlainFragmentAdapter adapter = new PlainFragmentAdapter(Plain.this);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(adapter);
		mPager.setCurrentItem(0);

		mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.plains_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			i = new Intent(getApplicationContext(), About.class);
			startActivity(i);
			break;
		case R.id.mShare:
			i = new Intent(android.content.Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(android.content.Intent.EXTRA_TEXT,
					getString(R.string.share_message));
			startActivity(Intent.createChooser(i, "Invite friends using..."));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

}