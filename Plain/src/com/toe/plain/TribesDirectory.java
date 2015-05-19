package com.toe.plain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.view.Window;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;
import com.toe.plain.XListView.IXListViewListener;

public class TribesDirectory extends SherlockFragmentActivity {

	ArrayList<TribeListItem> tribes = new ArrayList<TribeListItem>();
	TribeListItemAdapter adapter;
	Intent i;
	XListView listView;
	StorageService storageService;
	String name, description, lastTribe, error;
	ArrayList<String> jsonDocArray, jsonIdArray, jsonTimesArray;
	ShimmerTextView tvNoListItem;
	NewTribeCustomDialog ntcDialog;
	SherlockFragmentActivity activity;
	SharedPreferences sp;
	TribeListCustomDialog tlcDialog;
	ArrayList<String> savedHashtags;
	boolean nameIsClean = true;
	boolean descriptionIsClean = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.list_view);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setSupportProgressBarIndeterminateVisibility(false);

		initialize();
		setUp();
		getCurrentTribe();
		getTribes();
	}

	private void initialize() {
		// TODO Auto-generated method stub
		App42API.initialize(getApplicationContext(),
				getString(R.string.api_key), getString(R.string.secret_key));
		storageService = App42API.buildStorageService();
	}

	private void setUp() {
		// TODO Auto-generated method stub
		activity = this;
		sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
		Typeface font = Typeface.createFromAsset(getAssets(),
				getString(R.string.font));

		tvNoListItem = (ShimmerTextView) findViewById(R.id.tvNoListItem);
		new Shimmer().start(tvNoListItem);
		tvNoListItem.setTypeface(font);
	}

	private void getCurrentTribe() {
		// TODO Auto-generated method stub
		lastTribe = sp.getString("tribeHashtag", "#tribes");
	}

	private void getTribes() {
		// TODO Auto-generated method stub
		setSupportProgressBarIndeterminateVisibility(true);

		HashMap<String, String> metaHeaders = new HashMap<String, String>();
		metaHeaders.put("orderByDescending", "_$createdAt");
		storageService.setOtherMetaHeaders(metaHeaders);
		storageService.findAllDocuments(getString(R.string.database_name),
				getString(R.string.tribes_collection), new App42CallBack() {
					public void onSuccess(Object response) {
						Storage storage = (Storage) response;
						ArrayList<Storage.JSONDocument> jsonDocList = storage
								.getJsonDocList();
						jsonDocArray = new ArrayList<String>();
						jsonIdArray = new ArrayList<String>();
						jsonTimesArray = new ArrayList<String>();

						for (int i = 0; i < jsonDocList.size(); i++) {
							jsonDocArray.add(jsonDocList.get(i).getJsonDoc());
							jsonIdArray.add(jsonDocList.get(i).getDocId());
							jsonTimesArray.add(jsonDocList.get(i)
									.getCreatedAt());
						}

						populateList(jsonDocArray, jsonIdArray, jsonTimesArray);
					}

					public void onException(Exception ex) {
						System.out.println("Exception Message"
								+ ex.getMessage());
						errorHandler(ex);
					}
				});
	}

	protected void populateList(final ArrayList<String> jsonDocArray,
			final ArrayList<String> jsonIdArray,
			final ArrayList<String> jsonTimesArray) {
		// TODO Auto-generated method stub
		tribes.clear();

		for (int i = 0; i < jsonDocArray.size(); i++) {
			try {
				JSONObject json = new JSONObject(jsonDocArray.get(i));
				tribes.add(new TribeListItem(json.getString("name"), json
						.getString("description"), json.getInt("likes"),
						jsonTimesArray.get(i)));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				tvNoListItem.setVisibility(View.INVISIBLE);

				listView = (XListView) findViewById(R.id.lvListItems);
				listView.setPullLoadEnable(false);
				activity.runOnUiThread(new Runnable() {
					public void run() {
						if (adapter == null) {
							adapter = new TribeListItemAdapter(
									getApplicationContext(),
									R.layout.list_item, tribes);
							SwingBottomInAnimationAdapter swing = new SwingBottomInAnimationAdapter(
									adapter);
							swing.setAbsListView(listView);
							listView.setAdapter(swing);
						} else {
							adapter.notifyDataSetChanged();
							SwingBottomInAnimationAdapter swing = new SwingBottomInAnimationAdapter(
									adapter);
							swing.setAbsListView(listView);
						}
					}
				});
				setSupportProgressBarIndeterminateVisibility(false);
				listView.setXListViewListener(new IXListViewListener() {

					@Override
					public void onRefresh() {
						// TODO Auto-generated method stub
						getTribes();
					}

					@Override
					public void onLoadMore() {
						// TODO Auto-generated method stub

					}
				});
				listView.stopRefresh();
				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						saveHashtag(tribes.get(arg2 - 1).getName());
						i = new Intent(getApplicationContext(), Tribes.class);
						Bundle b = new Bundle();
						b.putString("tribe", tribes.get(arg2 - 1).getName());
						i.putExtras(b);
						startActivity(i);
					}
				});
				listView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						// TODO Auto-generated method stub
						try {
							addLike(jsonIdArray.get(arg2 - 1), new JSONObject(
									jsonDocArray.get(arg2 - 1))
									.getString("name"), new JSONObject(
									jsonDocArray.get(arg2 - 1))
									.getString("description"), new JSONObject(
									jsonDocArray.get(arg2 - 1)).getInt("likes"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return true;
					}
				});
			}
		});
	}

	private void addLike(String jsonDocId, String name, String description,
			int currentLikes) {
		// TODO Auto-generated method stub
		setSupportProgressBarIndeterminateVisibility(false);
		Toast.makeText(getApplicationContext(), "Liking...", Toast.LENGTH_SHORT)
				.show();

		JSONObject likedStory = new JSONObject();
		try {
			likedStory.put("name", name);
			likedStory.put("description", description);
			likedStory.put("likes", currentLikes + 1);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		storageService.updateDocumentByDocId(getString(R.string.database_name),
				getString(R.string.tribes_collection), jsonDocId, likedStory,
				new App42CallBack() {
					public void onSuccess(Object response) {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								setSupportProgressBarIndeterminateVisibility(false);
								Toast.makeText(getApplicationContext(),
										"Liked!", Toast.LENGTH_SHORT).show();
								getTribes();
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

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					tvNoListItem.setVisibility(View.VISIBLE);
					tvNoListItem.setText("Refresh");
					tvNoListItem.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							getTribes();
						}
					});
				}
			});
		} else if (ex.getMessage().contains("No document")) {
			error = "No tribes have been listed yet :-(";

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					tvNoListItem.setVisibility(View.VISIBLE);
					tvNoListItem.setText("Refresh");
					tvNoListItem.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							getTribes();
						}
					});
				}
			});
		} else {
			error = ex.getMessage();
		}

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				setSupportProgressBarIndeterminateVisibility(false);
				Toast.makeText(getApplicationContext(), error,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private boolean filterWords(String story) {
		// TODO Auto-generated method stub
		String bannedWords[] = getResources().getStringArray(
				R.array.banned_words);

		for (int i = 0; i < bannedWords.length; i++) {
			if (story.contains(bannedWords[i])) {
				nameIsClean = false;
			}
		}
		return nameIsClean;
	}

	private void publishTribe(String name, String description) {
		// TODO Auto-generated method stub
		setSupportProgressBarIndeterminateVisibility(true);
		Toast.makeText(getApplicationContext(), "Just a moment",
				Toast.LENGTH_SHORT).show();

		if (!name.startsWith("#")) {
			name = "#" + name;
		}

		JSONObject jsonStory = new JSONObject();
		try {
			jsonStory.put("name", name.toLowerCase());
			jsonStory.put("description", description);
			jsonStory.put("likes", 0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		storageService.insertJSONDocument(getString(R.string.database_name),
				getString(R.string.tribes_collection), jsonStory,
				new App42CallBack() {
					public void onSuccess(Object response) {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								setSupportProgressBarIndeterminateVisibility(false);
								Toast.makeText(getApplicationContext(),
										"Tribe published!", Toast.LENGTH_SHORT)
										.show();
								getTribes();
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

	@SuppressWarnings("unchecked")
	private void getSavedHashtags() {
		// TODO Auto-generated method stub
		try {
			savedHashtags = (ArrayList<String>) ObjectSerializer
					.deserialize(sp.getString("savedHashtags",
							ObjectSerializer.serialize(new ArrayList<String>())));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		@SuppressWarnings("rawtypes")
		HashSet hashSet = new HashSet();
		hashSet.addAll(savedHashtags);
		savedHashtags.clear();
		savedHashtags.addAll(hashSet);

		Collections.sort(savedHashtags);
	}

	protected void saveHashtag(String hashtag) {
		// TODO Auto-generated method stub
		getSavedHashtags();

		savedHashtags.add(hashtag);

		try {
			sp.edit()
					.putString("savedHashtags",
							ObjectSerializer.serialize(savedHashtags)).commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.tribes_directory_menu, menu);
		SubMenu subMenu = menu.addSubMenu("Options");
		subMenu.add(0, 0, 0, "Menu:");
		subMenu.add(1, 1, 1, "Your tribes");
		subMenu.add(2, 2, 2, "Tribes?");

		MenuItem subMenuItem = subMenu.getItem();
		subMenuItem.setIcon(R.drawable.more_menu_icon);
		subMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.mNewTribe:
			ntcDialog = new NewTribeCustomDialog(activity);
			ntcDialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			ntcDialog.show();
			ntcDialog.bDone.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					name = ntcDialog.etName.getText().toString().trim();
					description = ntcDialog.etDescription.getText().toString()
							.trim();
					nameIsClean = true;
					nameIsClean = filterWords(name);
					descriptionIsClean = true;
					descriptionIsClean = filterWords(description);

					if (nameIsClean && descriptionIsClean) {
						if (description.length() < 100) {
							publishTribe(name, description);
							ntcDialog.dismiss();
						} else {
							ntcDialog.etDescription
									.setError("Please enter a description with less than 100 characters");
						}
					}
				}
			});
			break;
		case 1:
			getSavedHashtags();
			tlcDialog = new TribeListCustomDialog(activity);
			tlcDialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			tlcDialog.savedHashtags = new ArrayList<String>();
			tlcDialog.savedHashtags = savedHashtags;
			tlcDialog.show();
			tlcDialog.listView
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							String hashtag = tlcDialog.tribes.get(arg2)
									.getName();
							sp.edit().putString("tribeHashtag", hashtag)
									.commit();
							tlcDialog.dismiss();

							i = new Intent(getApplicationContext(),
									Tribes.class);
							Bundle b = new Bundle();
							b.putString("tribe", hashtag);
							i.putExtras(b);
							startActivity(i);
						}
					});
			tlcDialog.listView
					.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub
							savedHashtags.remove(arg2);
							try {
								sp.edit()
										.putString(
												"savedHashtags",
												ObjectSerializer
														.serialize(savedHashtags))
										.commit();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							Toast.makeText(getApplicationContext(),
									"Tribe deleted", Toast.LENGTH_SHORT).show();
							tlcDialog.dismiss();
							return false;
						}
					});
			break;
		case 2:
			i = new Intent(getApplicationContext(), AboutTribes.class);
			startActivity(i);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
