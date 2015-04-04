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
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder.Operator;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;
import com.toe.plain.XListView.IXListViewListener;

public class Tribes extends SherlockFragmentActivity {

	ArrayList<ListItem> stories = new ArrayList<ListItem>();
	Intent i;
	ListItemAdapter adapter;
	XListView listView;
	StorageService storageService;
	String hashtag, error;
	ArrayList<String> jsonDocArray, jsonIdArray;
	ShimmerTextView tvNoListItem;
	StoryOptionsCustomDialog socDialog;
	SherlockFragmentActivity activity;
	ArrayList<String> favouriteStories = new ArrayList<String>();
	ArrayList<Integer> favouriteLikes = new ArrayList<Integer>();
	ArrayList<String> favouriteTags = new ArrayList<String>();
	ArrayList<Boolean> favouriteAdmins = new ArrayList<Boolean>();
	SharedPreferences sp;
	EditDataCustomDialog edcDialog;
	TribeListCustomDialog tlcDialog;
	String defaultTribe = "#tribes";
	boolean storyIsClean = true;
	ArrayList<String> savedHashtags;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.list_view);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setSupportProgressBarIndeterminateVisibility(false);

		initialize();
		setUp();
		getKeyword();

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

	private void getKeyword() {
		// TODO Auto-generated method stub
		hashtag = sp.getString("tribeHashtag", null);

		if (hashtag == null) {
			getSupportActionBar().setTitle(defaultTribe);
			fetchResults(defaultTribe);
		} else {
			getSupportActionBar().setTitle(hashtag);
			fetchResults(hashtag);
		}
	}

	private void fetchResults(String keyword) {
		// TODO Auto-generated method stub
		setSupportProgressBarIndeterminateVisibility(true);

		String key = "story";
		Query query = QueryBuilder.build(key, keyword + " ", Operator.LIKE);

		HashMap<String, String> metaHeaders = new HashMap<String, String>();
		metaHeaders.put("orderByDescending", "_$createdAt");
		storageService.setOtherMetaHeaders(metaHeaders);
		storageService.findDocumentsByQuery(getString(R.string.database_name),
				getString(R.string.forums_collection), query,
				new App42CallBack() {
					public void onSuccess(Object response) {
						Storage storage = (Storage) response;
						ArrayList<Storage.JSONDocument> jsonDocList = storage
								.getJsonDocList();

						jsonDocArray = new ArrayList<String>();
						jsonIdArray = new ArrayList<String>();

						for (int i = 0; i < jsonDocList.size(); i++) {
							jsonDocArray.add(jsonDocList.get(i).getJsonDoc());
							jsonIdArray.add(jsonDocList.get(i).getDocId());
						}
						populateList(jsonDocArray, jsonIdArray);
					}

					public void onException(Exception ex) {
						System.out.println("Exception Message"
								+ ex.getMessage());
						errorHandler(ex);
					}
				});
	}

	protected void populateList(final ArrayList<String> jsonDocArray,
			final ArrayList<String> jsonIdArray) {
		// TODO Auto-generated method stub
		stories.clear();

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
							adapter = new ListItemAdapter(
									getApplicationContext(),
									R.layout.list_item, stories);
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
						getKeyword();
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
						try {
							addLike(jsonIdArray.get(arg2 - 1),
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
							View arg1, final int arg2, long arg3) {
						// TODO Auto-generated method stub
						socDialog = new StoryOptionsCustomDialog(activity);
						socDialog.getWindow().setBackgroundDrawable(
								new ColorDrawable(Color.TRANSPARENT));
						socDialog.show();
						socDialog.reply
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										edcDialog = new EditDataCustomDialog(
												activity);
										edcDialog
												.getWindow()
												.setBackgroundDrawable(
														new ColorDrawable(
																Color.TRANSPARENT));
										String tag = null;

										if (stories.get(arg2 - 1).isAdmin()) {
											tag = "dev";
										} else {
											tag = stories.get(arg2 - 1)
													.getTag().toLowerCase();
										}

										edcDialog.title = "Reply to @" + tag;
										edcDialog.tag = hashtag + " @" + tag
												+ " ";

										edcDialog.show();
										edcDialog.bDone
												.setOnClickListener(new OnClickListener() {

													@Override
													public void onClick(View v) {
														// TODO Auto-generated
														// method
														// stub
														String story = edcDialog.etDataField
																.getText()
																.toString()
																.trim();
														storyIsClean = true;
														storyIsClean = filterWords(story);

														if (storyIsClean) {
															if (story.length() > 1) {
																edcDialog.etDataField
																		.setEnabled(false);
																publishStory(story);
																edcDialog
																		.dismiss();
															} else {
																edcDialog.etDataField
																		.setError("Please say something...");
															}
														} else {
															edcDialog.etDataField
																	.setError(getString(R.string.et_not_clean_error));
														}
													}
												});
										socDialog.dismiss();
									}
								});
						socDialog.share
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										i = new Intent(
												android.content.Intent.ACTION_SEND);
										i.setType("text/plain");
										try {
											i.putExtra(
													android.content.Intent.EXTRA_TEXT,
													"\""
															+ new JSONObject(
																	jsonDocArray
																			.get(arg2 - 1))
																	.getString("story")
															+ "\"\n\n- 'Plain");
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										startActivity(Intent.createChooser(i,
												"Share the plain using..."));
									}
								});
						socDialog.favourite
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										try {
											String story = new JSONObject(
													jsonDocArray.get(arg2 - 1))
													.getString("story");
											int likes = new JSONObject(
													jsonDocArray.get(arg2 - 1))
													.getInt("likes");
											String tag = new JSONObject(
													jsonDocArray.get(arg2 - 1))
													.getString("tag");
											boolean admin = new JSONObject(
													jsonDocArray.get(arg2 - 1))
													.getBoolean("admin");

											favouriteStory(story, likes, tag,
													admin);

										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								});
						return false;
					}
				});
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void getFavourites() {
		// TODO Auto-generated method stub
		try {
			favouriteStories = (ArrayList<String>) ObjectSerializer
					.deserialize(sp
							.getString("favouriteStories", ObjectSerializer
									.serialize(new ArrayList<String>())));
			favouriteLikes = (ArrayList<Integer>) ObjectSerializer
					.deserialize(sp.getString("favouriteLikes",
							ObjectSerializer
									.serialize(new ArrayList<Integer>())));
			favouriteTags = (ArrayList<String>) ObjectSerializer
					.deserialize(sp.getString("favouriteTags",
							ObjectSerializer.serialize(new ArrayList<String>())));
			favouriteAdmins = (ArrayList<Boolean>) ObjectSerializer
					.deserialize(sp.getString("favouriteAdmins",
							ObjectSerializer
									.serialize(new ArrayList<Boolean>())));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void favouriteStory(String story, int likes, String tag,
			boolean admin) {
		// TODO Auto-generated method stub
		getFavourites();

		if (story != null) {
			try {
				favouriteStories.add(0, story);
				favouriteLikes.add(0, likes);
				favouriteTags.add(0, tag);
				favouriteAdmins.add(0, admin);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.toString());
			}
		}

		try {
			sp.edit()
					.putString("favouriteStories",
							ObjectSerializer.serialize(favouriteStories))
					.commit();
			sp.edit()
					.putString("favouriteLikes",
							ObjectSerializer.serialize(favouriteLikes))
					.commit();
			sp.edit()
					.putString("favouriteTags",
							ObjectSerializer.serialize(favouriteTags)).commit();
			sp.edit()
					.putString("favouriteAdmins",
							ObjectSerializer.serialize(favouriteAdmins))
					.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}

		socDialog.dismiss();
		Toast.makeText(getApplicationContext(), "Favourited!",
				Toast.LENGTH_SHORT).show();
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
				getString(R.string.forums_collection), jsonDocId, likedStory,
				new App42CallBack() {
					public void onSuccess(Object response) {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								setSupportProgressBarIndeterminateVisibility(false);
								Toast.makeText(getApplicationContext(),
										"Liked!", Toast.LENGTH_SHORT).show();
								getKeyword();
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
							getKeyword();
						}
					});
				}
			});
		} else if (ex.getMessage().contains("No document")) {
			error = "No plains found for " + hashtag + " :-(";

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
							getKeyword();
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
				storyIsClean = false;
			}
		}
		return storyIsClean;
	}

	private void publishStory(String story) {
		// TODO Auto-generated method stub
		setSupportProgressBarIndeterminateVisibility(true);
		Toast.makeText(getApplicationContext(), "Just a moment",
				Toast.LENGTH_SHORT).show();

		JSONObject jsonStory = new JSONObject();
		try {
			String tag = RandomStringUtils.random(3, true, true);
			jsonStory.put("story", story);
			jsonStory.put("likes", 0);
			jsonStory.put("tag", tag);
			jsonStory.put("admin", false);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		storageService.insertJSONDocument(getString(R.string.database_name),
				getString(R.string.forums_collection), jsonStory,
				new App42CallBack() {
					public void onSuccess(Object response) {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								edcDialog.etDataField.setEnabled(true);
								edcDialog.etDataField.setText("");
								setSupportProgressBarIndeterminateVisibility(false);
								Toast.makeText(getApplicationContext(),
										"Plain published!", Toast.LENGTH_SHORT)
										.show();
								getKeyword();
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
		getSupportMenuInflater().inflate(R.menu.tribes_menu, menu);

		SubMenu subMenu = menu.addSubMenu("Options");
		subMenu.add(0, 0, 0, "Menu:");
		subMenu.add(1, 1, 1, "Switch tribes");
		subMenu.add(2, 2, 2, "Your tribes");
		subMenu.add(3, 3, 3, "Invite members");
		subMenu.add(4, 4, 4, "Tribes??");

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
		case R.id.mNewPlain:
			if (hashtag == null) {
				getKeyword();
			} else {
				edcDialog = new EditDataCustomDialog(activity);
				edcDialog.getWindow().setBackgroundDrawable(
						new ColorDrawable(Color.TRANSPARENT));
				edcDialog.title = "Say something...";
				edcDialog.tag = hashtag + " ";
				edcDialog.show();
				edcDialog.bDone.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String story = edcDialog.etDataField.getText()
								.toString().trim();
						storyIsClean = true;
						storyIsClean = filterWords(story);

						if (story.contains("#")) {
							if (storyIsClean) {
								publishStory(story);
								edcDialog.etDataField.setEnabled(false);
								edcDialog.dismiss();
								getKeyword();
							} else {
								edcDialog.etDataField
										.setError(getString(R.string.et_not_clean_error));
							}
						} else {
							edcDialog.etDataField
									.setError("Try including the tribe hashtag");
						}
					}
				});
			}
			break;
		case 1:
			edcDialog = new EditDataCustomDialog(activity);
			edcDialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			edcDialog.title = "Create or join a tribe";
			edcDialog.tag = "#";
			edcDialog.show();
			edcDialog.bDone.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String hashtag = edcDialog.etDataField.getText().toString()
							.trim();
					storyIsClean = true;
					storyIsClean = filterWords(hashtag);

					if (hashtag.contains("#")) {
						if (storyIsClean) {
							if (hashtag.length() > 2) {
								sp.edit().putString("tribeHashtag", hashtag)
										.commit();
								edcDialog.dismiss();
								getKeyword();
								saveHashtag(hashtag);
							} else {
								edcDialog.etDataField
										.setError("Lol. At least 3 characters");
							}
						} else {
							edcDialog.etDataField
									.setError(getString(R.string.et_not_clean_error));
						}
					} else {
						edcDialog.etDataField
								.setError("Tribe names start with a '#'");
					}
				}
			});
			break;
		case 2:
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
									.getTribe();
							sp.edit().putString("tribeHashtag", hashtag)
									.commit();
							storyIsClean = true;
							storyIsClean = filterWords(hashtag);
							getKeyword();
							tlcDialog.dismiss();
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
		case 3:
			String tribeHashtag = sp.getString("tribeHashtag", null);
			if (tribeHashtag == null) {
				tribeHashtag = defaultTribe;
			}
			i = new Intent(android.content.Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(android.content.Intent.EXTRA_TEXT,
					"Join my tribe on 'Plain " + tribeHashtag);
			startActivity(Intent
					.createChooser(i, "Invite tribe members via..."));
			break;
		case 4:
			i = new Intent(getApplicationContext(), AboutTribes.class);
			startActivity(i);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
