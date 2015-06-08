package com.toe.plain;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView.OnEmojiconClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnEmojiconBackspaceClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnSoftKeyboardOpenCloseListener;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
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
	ArrayList<String> jsonDocArray, jsonIdArray, jsonTimesArray;
	ShimmerTextView tvNoListItem;
	StoryOptionsCustomDialog socDialog;
	SherlockFragmentActivity activity;
	ArrayList<String> storedTags;
	ArrayList<String> favouriteStories = new ArrayList<String>();
	ArrayList<Integer> favouriteLikes = new ArrayList<Integer>();
	ArrayList<String> favouriteTags = new ArrayList<String>();
	ArrayList<Boolean> favouriteAdmins = new ArrayList<Boolean>();
	ArrayList<String> favouriteTimestamps = new ArrayList<String>();
	SharedPreferences sp;
	EditDataCustomDialog edcDialog;
	TribeListCustomDialog tlcDialog;
	boolean storyIsClean = true;
	ArrayList<String> savedHashtags;
	EmojiconEditText emojiconEditText;
	View rootView;
	ImageView emojiButton;
	ImageView submitButton;
	EmojiconsPopup popup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.plains_list_view);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setSupportProgressBarIndeterminateVisibility(false);

		initialize();
		setUp();
		getTribe();
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
		setUpEmojiKeyboard();
		Typeface font = Typeface.createFromAsset(getAssets(),
				getString(R.string.font));

		tvNoListItem = (ShimmerTextView) findViewById(R.id.tvNoListItem);
		new Shimmer().start(tvNoListItem);
		tvNoListItem.setTypeface(font);
	}

	private void setUpEmojiKeyboard() {
		// TODO Auto-generated method stub
		emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
		rootView = findViewById(R.id.root_view);
		emojiButton = (ImageView) findViewById(R.id.emoji_btn);
		submitButton = (ImageView) findViewById(R.id.submit_btn);
		popup = new EmojiconsPopup(rootView, this);

		popup.setSizeForSoftKeyboard();
		popup.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {

			@Override
			public void onEmojiconClicked(Emojicon emojicon) {
				emojiconEditText.append(emojicon.getEmoji());
			}
		});
		popup.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {

			@Override
			public void onEmojiconBackspaceClicked(View v) {
				KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0,
						0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
				emojiconEditText.dispatchKeyEvent(event);
			}
		});
		popup.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				changeEmojiKeyboardIcon(emojiButton, R.drawable.smiley);
			}
		});
		popup.setOnSoftKeyboardOpenCloseListener(new OnSoftKeyboardOpenCloseListener() {

			@Override
			public void onKeyboardOpen(int keyBoardHeight) {

			}

			@Override
			public void onKeyboardClose() {
				if (popup.isShowing())
					popup.dismiss();
			}
		});
		popup.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {

			@Override
			public void onEmojiconClicked(Emojicon emojicon) {
				emojiconEditText.append(emojicon.getEmoji());
			}
		});
		popup.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {

			@Override
			public void onEmojiconBackspaceClicked(View v) {
				KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0,
						0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
				emojiconEditText.dispatchKeyEvent(event);
			}
		});
		emojiButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!popup.isShowing()) {

					if (popup.isKeyBoardOpen()) {
						popup.showAtBottom();
						changeEmojiKeyboardIcon(emojiButton,
								R.drawable.ic_action_keyboard);
					}

					else {
						emojiconEditText.setFocusableInTouchMode(true);
						emojiconEditText.requestFocus();
						popup.showAtBottomPending();
						final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						inputMethodManager.showSoftInput(emojiconEditText,
								InputMethodManager.SHOW_IMPLICIT);
						changeEmojiKeyboardIcon(emojiButton,
								R.drawable.ic_action_keyboard);
					}
				}

				else {
					popup.dismiss();
				}
			}
		});

		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String story = emojiconEditText.getText().toString().trim();
				storyIsClean = true;
				storyIsClean = filterWords(story);

				if (story.contains(hashtag)) {
					if (storyIsClean) {
						if (story.length() > 1) {
							if (story.length() < 1000) {
								publishStory(story);
							} else {
								emojiconEditText
										.setError("Try shortening that a bit...");
							}
						} else {
							emojiconEditText
									.setError("Please say something...");
						}
					} else {
						emojiconEditText
								.setError(getString(R.string.et_not_clean_error));
					}
				} else {
					emojiconEditText.setError("Please make sure " + hashtag
							+ " is included");
				}
			}
		});
	}

	private void changeEmojiKeyboardIcon(ImageView iconToBeChanged,
			int drawableResourceId) {
		iconToBeChanged.setImageResource(drawableResourceId);
	}

	private void getTribe() {
		// TODO Auto-generated method stub
		Bundle b = getIntent().getExtras();
		hashtag = b.getString("tribe");

		fetchResults(hashtag);
	}

	private void fetchResults(String keyword) {
		// TODO Auto-generated method stub
		keyword = keyword.toLowerCase().replaceAll("\\s+", "");

		emojiconEditText.setText(keyword + " ");
		getSupportActionBar().setTitle(keyword);
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
		stories.clear();

		for (int i = 0; i < jsonDocArray.size(); i++) {
			try {
				JSONObject json = new JSONObject(jsonDocArray.get(i));
				stories.add(new ListItem(json.getString("story"), json
						.getInt("likes"), json.getString("tag"), json
						.getBoolean("admin"), jsonTimesArray.get(i)));
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
									getApplicationContext(), activity,
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
						getTribe();
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
											.getBoolean("admin"),
									new JSONObject(jsonDocArray.get(arg2 - 1))
											.getString("username"),
									new JSONObject(jsonDocArray.get(arg2 - 1))
											.getString("screenName"));
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
										String tag = null;

										if (stories.get(arg2 - 1).isAdmin()) {
											tag = "dev";
										} else {
											tag = stories.get(arg2 - 1)
													.getTag().toLowerCase();
										}

										emojiconEditText.setText(hashtag + " @"
												+ tag + " ");

										socDialog.dismiss();
									}
								});
						socDialog.replain
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										replainStory(stories.get(arg2 - 1)
												.getStory(),
												stories.get(arg2 - 1)
														.getLikes(),
												stories.get(arg2 - 1).getTag(),
												stories.get(arg2 - 1).isAdmin());
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
										String story = stories.get(arg2 - 1)
												.getStory();
										int likes = stories.get(arg2 - 1)
												.getLikes();
										String tag = stories.get(arg2 - 1)
												.getTag();
										boolean admin = stories.get(arg2 - 1)
												.isAdmin();
										String timestamp = stories
												.get(arg2 - 1).getTimestamp();

										favouriteStory(story, likes, tag,
												admin, timestamp);
									}
								});
						socDialog.favourite
								.setOnLongClickListener(new OnLongClickListener() {

									@Override
									public boolean onLongClick(View v) {
										// TODO Auto-generated method stub
										if ((arg2 - 1) < 100) {
											boolean belongsToUser = false;
											String tag = stories.get(arg2 - 1)
													.getTag();

											ArrayList<String> tags = getTags();
											for (int i = 0; i < tags.size(); i++) {
												if (tags.get(i).equals(
														tag.toLowerCase())) {
													belongsToUser = true;
												}
											}

											if (belongsToUser) {
												runOnUiThread(new Runnable() {
													public void run() {
														Toast.makeText(
																getApplicationContext(),
																"Deleting...",
																Toast.LENGTH_SHORT)
																.show();
													}
												});
												storageService
														.deleteDocumentById(
																getString(R.string.database_name),
																getString(R.string.forums_collection),
																jsonIdArray
																		.get(arg2 - 1),
																new App42CallBack() {
																	public void onSuccess(
																			Object response) {
																		runOnUiThread(new Runnable() {
																			public void run() {
																				socDialog
																						.dismiss();
																				Toast.makeText(
																						getApplicationContext(),
																						"Plain deleted!",
																						Toast.LENGTH_SHORT)
																						.show();
																				getTribe();
																			}
																		});
																	}

																	public void onException(
																			Exception ex) {
																		System.out
																				.println("Exception Message"
																						+ ex.getMessage());
																		errorHandler(ex);
																	}
																});
											} else {
												runOnUiThread(new Runnable() {
													public void run() {
														Toast.makeText(
																getApplicationContext(),
																"You can only delete your own plains",
																Toast.LENGTH_SHORT)
																.show();
													}
												});
											}
										} else {
											runOnUiThread(new Runnable() {
												public void run() {
													Toast.makeText(
															getApplicationContext(),
															"Sorry too late to do that :-(",
															Toast.LENGTH_SHORT)
															.show();
												}
											});
										}
										return true;
									}
								});
						socDialog.chat
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										String username = sp.getString(
												"username", null);
										String password = sp.getString(
												"password", null);
										String screenName = sp.getString(
												"screenName", null);

										Toast.makeText(
												getApplicationContext(),
												"Coming soon! Listen to some Lorde before that happens...",
												Toast.LENGTH_SHORT).show();
										socDialog.dismiss();
									}
								});
						return true;
					}
				});
			}
		});
	}

	private void replainStory(String story, int likes, String tag, boolean admin) {
		// TODO Auto-generated method stub
		setSupportProgressBarIndeterminateVisibility(true);
		Toast.makeText(getApplicationContext(), "Just a moment",
				Toast.LENGTH_SHORT).show();

		if (admin) {
			tag = "rp@dev";
		}

		if (!tag.contains("rp@")) {
			tag = "rp@" + tag;
		}

		JSONObject jsonStory = new JSONObject();
		try {
			jsonStory.put("story", story);
			jsonStory.put("likes", likes);
			jsonStory.put("tag", tag);
			jsonStory.put("admin", admin);
			jsonStory.put("username", sp.getString("username", null));
			jsonStory.put("screenName", sp.getString("screenName", null));
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
								setSupportProgressBarIndeterminateVisibility(false);
								emojiconEditText.setText("");
								Toast.makeText(getApplicationContext(),
										"Plain published!", Toast.LENGTH_SHORT)
										.show();
								getTribe();
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
			favouriteTimestamps = (ArrayList<String>) ObjectSerializer
					.deserialize(sp
							.getString("favouriteTimestamps", ObjectSerializer
									.serialize(new ArrayList<String>())));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void favouriteStory(String story, int likes, String tag,
			boolean admin, String timestamp) {
		// TODO Auto-generated method stub
		getFavourites();

		if (story != null) {
			try {
				favouriteStories.add(0, story);
				favouriteLikes.add(0, likes);
				favouriteTags.add(0, tag);
				favouriteAdmins.add(0, admin);
				favouriteTimestamps.add(0, timestamp);
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
			sp.edit()
					.putString("favouriteTimestamps",
							ObjectSerializer.serialize(favouriteTimestamps))
					.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}

		socDialog.dismiss();
		Toast.makeText(getApplicationContext(), "Favourited!",
				Toast.LENGTH_SHORT).show();
	}

	private void addLike(String jsonDocId, int currentLikes, String story,
			String tag, boolean admin, String username, String screenName) {
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
			likedStory.put("username", username);
			likedStory.put("screenName", screenName);
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
								getTribe();
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
							getTribe();
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
							getTribe();
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
			jsonStory.put("username", sp.getString("username", null));
			jsonStory.put("screenName", sp.getString("screenName", null));

			storeTag(tag.toLowerCase());
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
								setSupportProgressBarIndeterminateVisibility(false);
								Toast.makeText(getApplicationContext(),
										"Plain published!", Toast.LENGTH_SHORT)
										.show();
								emojiconEditText.setText(hashtag + " ");
								getTribe();
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

	private void storeTag(String tag) {
		// TODO Auto-generated method stub
		storedTags = getTags();

		try {
			storedTags.add(0, tag);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
		}

		try {
			sp.edit()
					.putString("storedTribeTags",
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
					.deserialize(sp
							.getString("storedTribeTags", ObjectSerializer
									.serialize(new ArrayList<String>())));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return storedTags;
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
		SubMenu subMenu = menu.addSubMenu("Options");
		subMenu.add(0, 0, 0, "Menu:");
		subMenu.add(1, 1, 1, "Switch tribes");
		subMenu.add(2, 2, 2, "Your tribes");
		subMenu.add(3, 3, 3, "Invite members");

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
								saveHashtag(hashtag);
								fetchResults(hashtag);
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
									.getName();
							sp.edit().putString("tribeHashtag", hashtag)
									.commit();
							fetchResults(hashtag);
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
			String tribeHashtag = sp.getString("tribeHashtag", "#tribes");
			i = new Intent(android.content.Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(android.content.Intent.EXTRA_TEXT,
					"Join my tribe on 'Plain " + tribeHashtag);
			startActivity(Intent.createChooser(i, "Invite " + tribeHashtag
					+ " members via..."));
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
