package com.toe.plain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CacheManager;
import com.shephertz.app42.paas.sdk.android.App42CacheManager.Policy;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.OrderByType;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder.Operator;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;
import com.tjeannin.apprate.AppRate;
import com.toe.plain.PullToRefreshListView.OnRefreshListener;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

@SuppressWarnings("deprecation")
public class Plain extends SherlockFragmentActivity implements
		EmojiconGridFragment.OnEmojiconClickedListener,
		EmojiconsFragment.OnEmojiconBackspaceClickedListener {

	ArrayList<ListItem> stories = new ArrayList<ListItem>();
	ArrayList<ListItem> favourites;
	PlainFragmentAdapter mAdapter;
	ViewPager mPager;
	Intent i;
	PageIndicator mIndicator;
	ListItemAdapter adapter, favouritesAdapter;
	PullToRefreshListView listView;
	PullToRefreshListView favouritesListView;
	StorageService storageService;
	String error;
	EditText etStory;
	SherlockFragmentActivity activity;
	ShimmerTextView tvNoListItem, tvNoFavouriteListItem;
	SharedPreferences sp;
	Button bShare, bFavourite;
	ImageView ivHandle;
	ArrayList<String> favouriteStories = new ArrayList<String>();
	ArrayList<Integer> favouriteLikes = new ArrayList<Integer>();
	ArrayList<String> favouriteTags = new ArrayList<String>();
	ArrayList<Boolean> favouriteAdmins = new ArrayList<Boolean>();
	ArrayList<String> storedTags = new ArrayList<String>();
	ArrayList<String> jsonDocArray, jsonIdArray;
	StoryOptionsCustomDialog socDialog;
	FavouriteOptionsCustomDialog fsocDialog;
	ExitCustomDialog ecDialog;
	AlertDialog.Builder builder;
	AppRate rate;
	SlidingDrawer drawer;
	boolean storyIsClean = true;
	int animationDuration = 100000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.view_pager);
		getSupportActionBar().setHomeButtonEnabled(true);
		setSupportProgressBarIndeterminateVisibility(false);

		setAdapter();
		setUp();
		rateApp();
	}

	private void setUp() {
		// TODO Auto-generated method stub
		activity = this;
		sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

		App42API.initialize(getApplicationContext(),
				getString(R.string.api_key), getString(R.string.secret_key));
		App42CacheManager.setPolicy(Policy.NETWORK_FIRST);
		storageService = App42API.buildStorageService();

		setNotificationAlarm();
	}

	private void setNotificationAlarm() {
		// TODO Auto-generated method stub
		AlarmManager alarmManager = (AlarmManager) getApplicationContext()
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(),
				NotificationReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, intent, 0);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), 1800000, pendingIntent);
	}

	@SuppressLint({ "InlinedApi", "CutPasteId" })
	public void initPagerView(int position, final View view) {

		Typeface font = Typeface.createFromAsset(getAssets(),
				getString(R.string.font));

		switch (position) {
		case 0:
			final ImageView ivBackground = (ImageView) findViewById(R.id.ivBackground);

			Timer t = new Timer();
			t.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					backgroundAnimation(ivBackground);
				}
			}, 0, animationDuration + 1000);

			etStory = (EditText) findViewById(R.id.etStory);
			ivHandle = (ImageView) findViewById(R.id.handle);
			drawer = (SlidingDrawer) findViewById(R.id.emojiDrawer);
			drawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {

				@Override
				public void onDrawerOpened() {
					// TODO Auto-generated method stub
					ivHandle.setImageResource(R.drawable.emoji_icon_selected);
				}
			});
			drawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {

				@Override
				public void onDrawerClosed() {
					// TODO Auto-generated method stub
					ivHandle.setImageResource(R.drawable.emoji_icon);
				}
			});

			final FlipImageView ivDone = (FlipImageView) findViewById(R.id.ivDone);
			ivDone.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String story = etStory.getText().toString().trim();
					storyIsClean = true;
					storyIsClean = filterWords(story);

					if (storyIsClean) {
						if (story.length() > 1) {
							ivDone.setFlipped(true);
							etStory.setEnabled(false);
							publishStory(story);
						} else {
							etStory.setError("Please say something...");
						}
					} else {
						etStory.setError("Nice try. Try something less dirty now...");
					}
				}
			});
			break;
		case 1:
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
		case 2:
			tvNoFavouriteListItem = (ShimmerTextView) findViewById(R.id.tvNoListItem);
			new Shimmer().start(tvNoFavouriteListItem);
			tvNoFavouriteListItem.setTypeface(font);
			storedTags = getTags();

			break;
		case 3:
			tvNoFavouriteListItem = (ShimmerTextView) findViewById(R.id.tvNoListItem);
			new Shimmer().start(tvNoFavouriteListItem);
			tvNoFavouriteListItem.setTypeface(font);

			favouritesListView = (PullToRefreshListView) findViewById(R.id.lvListItems);
			favouritesListView.setOnRefreshListener(new OnRefreshListener() {

				@Override
				public void onRefresh() {
					// TODO Auto-generated method stub
					getFavourites();
					setFavourites();
					favouritesListView.invalidateViews();
					favouritesAdapter.notifyDataSetChanged();
					favouritesListView.onRefreshComplete();
				}
			});

			getFavourites();
			setFavourites();

			favouritesListView
					.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> arg0,
								View arg1, final int arg2, long arg3) {
							// TODO Auto-generated method stub
							fsocDialog = new FavouriteOptionsCustomDialog(
									activity);
							fsocDialog.getWindow().setBackgroundDrawable(
									new ColorDrawable(Color.TRANSPARENT));
							fsocDialog.show();
							fsocDialog.share
									.setOnClickListener(new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											i = new Intent(
													android.content.Intent.ACTION_SEND);
											i.setType("text/plain");
											i.putExtra(
													android.content.Intent.EXTRA_TEXT,
													"\""
															+ favouriteStories
																	.get(arg2 - 1)
															+ "\"\n\n- story from 'Plain");
											startActivity(Intent.createChooser(
													i,
													"Share the story using..."));
										}
									});
							fsocDialog.delete
									.setOnClickListener(new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											favouriteStories.remove(arg2 - 1);
											favouriteLikes.remove(arg2 - 1);
											favouriteTags.remove(arg2 - 1);
											favouriteAdmins.remove(arg2 - 1);

											try {
												sp.edit()
														.putString(
																"favouriteStories",
																ObjectSerializer
																		.serialize(favouriteStories))
														.commit();
												sp.edit()
														.putString(
																"favouriteLikes",
																ObjectSerializer
																		.serialize(favouriteLikes))
														.commit();
												sp.edit()
														.putString(
																"favouriteTags",
																ObjectSerializer
																		.serialize(favouriteTags))
														.commit();
												sp.edit()
														.putString(
																"favouriteAdmins",
																ObjectSerializer
																		.serialize(favouriteAdmins))
														.commit();
											} catch (IOException e) {
												e.printStackTrace();
											}

											getFavourites();
											setFavourites();
											// favouritesListView
											// .invalidateViews();
											favouritesAdapter
													.notifyDataSetChanged();
											Toast.makeText(
													getApplicationContext(),
													"Deleted",
													Toast.LENGTH_SHORT).show();
											fsocDialog.dismiss();
										}
									});
							return false;
						}
					});
			break;
		}
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

	private void backgroundAnimation(final ImageView ivBackground) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				RotateAnimation anim = new RotateAnimation(0.0f, 360.0f,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				anim.setInterpolator(new LinearInterpolator());
				anim.setRepeatCount(Animation.INFINITE);
				anim.setDuration(animationDuration);
				ivBackground.startAnimation(anim);
			}
		});
	}

	private void getData() {
		// TODO Auto-generated method stub
		setSupportProgressBarIndeterminateVisibility(true);

		HashMap<String, String> metaHeaders = new HashMap<String, String>();
		metaHeaders.put("orderByDescending", "_$createdAt");
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
						jsonDocArray = new ArrayList<String>();
						jsonIdArray = new ArrayList<String>();

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

	private void populateList(final ArrayList<String> jsonDocArray,
			final ArrayList<String> jsonIDArray) {
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

		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				tvNoListItem.setVisibility(View.INVISIBLE);
				setSupportProgressBarIndeterminateVisibility(false);

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
							View arg1, final int arg2, long arg3) {
						// TODO Auto-generated method stub
						socDialog = new StoryOptionsCustomDialog(activity);
						socDialog.getWindow().setBackgroundDrawable(
								new ColorDrawable(Color.TRANSPARENT));
						socDialog.show();
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
															+ "\"\n\n- story from 'Plain");
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										startActivity(Intent.createChooser(i,
												"Share the story using..."));
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

	private void setFavourites() {
		// TODO Auto-generated method stub
		favourites = new ArrayList<ListItem>();

		if (favouriteStories.size() > 0 && favouriteStories != null) {
			for (int i = 0; i < favouriteStories.size(); i++) {
				favourites.add(new ListItem(favouriteStories.get(i),
						favouriteLikes.get(i), favouriteTags.get(i),
						favouriteAdmins.get(i)));
			}
			tvNoFavouriteListItem.setVisibility(View.INVISIBLE);
		}
		favouritesAdapter = new ListItemAdapter(getApplicationContext(),
				R.layout.list_item, favourites);
		SwingBottomInAnimationAdapter swing = new SwingBottomInAnimationAdapter(
				favouritesAdapter);
		swing.setAbsListView(listView);
		favouritesListView.setAdapter(swing);
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
		getFavourites();
		setFavourites();
		favouritesListView.invalidateViews();
		favouritesAdapter.notifyDataSetChanged();
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

	private void storeTag(String tag) {
		// TODO Auto-generated method stub
		getFavourites();

		try {
			storedTags.add(0, tag);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
		}

		try {
			sp.edit()
					.putString("storedTags",
							ObjectSerializer.serialize(storedTags)).commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> getTags() {
		// TODO Auto-generated method stub
		try {
			storedTags = (ArrayList<String>) ObjectSerializer
					.deserialize(sp.getString("storedTags",
							ObjectSerializer.serialize(new ArrayList<String>())));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return storedTags;
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
			error = "No stories found :-(";
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

	private void rateApp() {
		// TODO Auto-generated method stub
		builder = new AlertDialog.Builder(Plain.this);
		rate = new AppRate(Plain.this);
		builder.setTitle("Hi, rate 'Plain?")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setMessage("Pretty pleeeeeeaaaaaaase!")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Intent i = new Intent(
										Intent.ACTION_VIEW,
										Uri.parse(getString(R.string.playstore_link)));
								startActivity(i);
								AppRate.reset(Plain.this);
								rate.setMinDaysUntilPrompt(120);
							}
						})
				.setNeutralButton("Later",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								AppRate.reset(Plain.this);
								rate.setMinDaysUntilPrompt(3);
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						AppRate.reset(Plain.this);
						rate.setMinDaysUntilPrompt(10);
					}
				});

		rate.setShowIfAppHasCrashed(false).setMinLaunchesUntilPrompt(10)
				.setCustomDialog(builder).init();
	}

	private void setAdapter() {
		// TODO Auto-generated method stub
		PlainFragmentAdapter adapter = new PlainFragmentAdapter(Plain.this);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(adapter);
		mPager.setCurrentItem(1);
		mPager.setOffscreenPageLimit(3);

		mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.plain_menu, menu);
		try {
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			SearchView searchView = (SearchView) menu.findItem(R.id.mSearch)
					.getActionView();

			if (searchView != null) {
				searchView.setSearchableInfo(searchManager
						.getSearchableInfo(getComponentName()));
				searchView.setIconifiedByDefault(true);
				searchView.setIconified(true);
				searchView
						.setQueryHint(Html
								.fromHtml("<font color = #ffffff>Keyword or tag...</font>"));
				searchView.clearFocus();
			}

			searchView.setOnQueryTextListener(new OnQueryTextListener() {

				@Override
				public boolean onQueryTextSubmit(String query) {
					// TODO Auto-generated method stub
					try {
						if (query.length() > 0) {
							executeQuery(query);
						} else {
							Toast.makeText(getApplicationContext(),
									"Haha, nice try", Toast.LENGTH_SHORT)
									.show();
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("Exception: " + e.toString());
					}
					return false;
				}

				@Override
				public boolean onQueryTextChange(String newText) {
					// TODO Auto-generated method stub
					return false;
				}
			});

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Exception on Search: " + e.toString());
		}
		return super.onCreateOptionsMenu(menu);
	}

	private void executeQuery(String queryString) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "Searching...",
				Toast.LENGTH_SHORT).show();

		String key1 = "story";
		String value1 = queryString;
		String key2 = "tag";
		String value2 = queryString;
		int max = 100;
		int offset = 0;

		Query q1 = QueryBuilder.build(key1, value1, Operator.LIKE);
		Query q2 = QueryBuilder.build(key2, value2, Operator.LIKE);
		Query query = QueryBuilder.compoundOperator(q1, Operator.OR, q2);
		storageService.findDocsWithQueryPagingOrderBy(
				getString(R.string.database_name),
				getString(R.string.collection_name), query, max, offset, key1,
				OrderByType.ASCENDING, new App42CallBack() {
					public void onSuccess(Object response) {
						Storage storage = (Storage) response;
						jsonDocArray = new ArrayList<String>();
						jsonIdArray = new ArrayList<String>();

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

							jsonDocArray.add(jsonDocList.get(i).getJsonDoc());
							jsonIdArray.add(jsonDocList.get(i).getDocId());
						}

						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (jsonDocArray.size() > 1) {
									Toast.makeText(
											activity,
											"Found " + jsonDocArray.size()
													+ " stories!",
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(activity, "Found a story!",
											Toast.LENGTH_SHORT).show();
								}

								populateList(jsonDocArray, jsonIdArray);
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			i = new Intent(getApplicationContext(), About.class);
			startActivity(i);
			break;
		case R.id.mExplore:
			i = new Intent(getApplicationContext(), Explore.class);
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
		if (drawer.isOpened()) {
			drawer.close();
		} else {
			ecDialog = new ExitCustomDialog(activity);
			ecDialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			ecDialog.show();
			ecDialog.bExitNo.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ecDialog.dismiss();
				}
			});
			ecDialog.bExitYes.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			});
		}
	}

	@Override
	public void onEmojiconClicked(Emojicon emojicon) {
		EmojiconsFragment.input(etStory, emojicon);
	}

	@Override
	public void onEmojiconBackspaceClicked(View v) {
		EmojiconsFragment.backspace(etStory);
	}

}