package com.toe.plain;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView.OnEmojiconClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnEmojiconBackspaceClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnSoftKeyboardOpenCloseListener;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.view.Window;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CacheManager;
import com.shephertz.app42.paas.sdk.android.App42CacheManager.Policy;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder.Operator;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;
import com.tjeannin.apprate.AppRate;
import com.toe.plain.XListView.IXListViewListener;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class Plain extends SherlockFragmentActivity {

	ArrayList<ListItem> stories = new ArrayList<ListItem>();
	ArrayList<ListItem> favourites;
	PlainFragmentAdapter mAdapter;
	ViewPager mPager;
	Intent i;
	PageIndicator mIndicator;
	ListItemAdapter adapter, repliesAdapter, favouritesAdapter;
	XListView listView, repliesListView, favouritesListView;
	StorageService storageService;
	String error;
	EditText etStory, etSearchForTag;
	Button bSearchForTag;
	SherlockFragmentActivity activity;
	ShimmerTextView tvNoListItem, tvNoReplyListItem, tvNoFavouriteListItem;
	SharedPreferences sp;
	Button bShare, bFavourite;
	ArrayList<String> favouriteStories = new ArrayList<String>();
	ArrayList<Integer> favouriteLikes = new ArrayList<Integer>();
	ArrayList<String> favouriteTags = new ArrayList<String>();
	ArrayList<Boolean> favouriteAdmins = new ArrayList<Boolean>();
	ArrayList<String> storedTags = new ArrayList<String>();
	ArrayList<String> fetchedTagStories = new ArrayList<String>();
	ArrayList<ListItem> replies = new ArrayList<ListItem>();
	ArrayList<ListItem> queryResults = new ArrayList<ListItem>();
	ArrayList<String> queryResultStories = new ArrayList<String>();
	ArrayList<Integer> queryResultLikes = new ArrayList<Integer>();
	ArrayList<String> queryResultTags = new ArrayList<String>();
	ArrayList<Boolean> queryResultAdmins = new ArrayList<Boolean>();
	ArrayList<String> jsonDocArray, jsonIdArray, appendJsonDocArray,
			appendJsonIdArray;
	StoryOptionsCustomDialog socDialog;
	ReplyOptionsCustomDialog rocDialog;
	FavouriteOptionsCustomDialog fsocDialog;
	EditDataCustomDialog edcDialog;
	ExitCustomDialog ecDialog;
	AlertDialog.Builder builder;
	AppRate rate;
	boolean storyIsClean = true;
	int animationDuration = 140000;
	int offset = 100;
	EmojiconEditText emojiconEditText, emojiconEditTextReplies;
	View rootView, rootViewReplies;
	ImageView emojiButton, emojiconButtonReplies;
	ImageView submitButton, submitButtonReplies;
	EmojiconsPopup popup, popupReplies;

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
			tvNoListItem = (ShimmerTextView) findViewById(R.id.tvNoListItem);
			new Shimmer().start(tvNoListItem);
			tvNoListItem.setTypeface(font);

			setUpEmojiKeyboard();
			getStories();
			listView = (XListView) findViewById(R.id.lvListItems);
			listView.setPullLoadEnable(true);
			listView.setXListViewListener(new IXListViewListener() {

				@Override
				public void onRefresh() {
					// TODO Auto-generated method stub
					getStories();
				}

				@Override
				public void onLoadMore() {
					// TODO Auto-generated method stub
					fetchMoreStories();
				}
			});
			break;
		case 1:
			tvNoReplyListItem = (ShimmerTextView) findViewById(R.id.tvNoListItem);
			new Shimmer().start(tvNoReplyListItem);
			tvNoReplyListItem.setTypeface(font);

			setUpEmojiKeyboardReplies();
			emojiconEditTextReplies.setVisibility(View.INVISIBLE);
			emojiconButtonReplies.setVisibility(View.INVISIBLE);
			submitButtonReplies.setVisibility(View.INVISIBLE);
			etSearchForTag = (EditText) findViewById(R.id.etSearchForTag);
			etSearchForTag.clearFocus();
			bSearchForTag = (Button) findViewById(R.id.bSearchForTag);
			bSearchForTag.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String tagQuery = etSearchForTag.getText().toString()
							.replace("@", "").trim();
					searchForTag(tagQuery);
				}
			});

			repliesListView = (XListView) findViewById(R.id.lvListItems);
			repliesListView.setPullLoadEnable(false);
			repliesListView.setXListViewListener(new IXListViewListener() {

				@Override
				public void onRefresh() {
					// TODO Auto-generated method stub
					getStoriesForReplies(getTags());
				}

				@Override
				public void onLoadMore() {
					// TODO Auto-generated method stub

				}
			});

			getStoriesForReplies(getTags());
			break;
		case 2:
			tvNoFavouriteListItem = (ShimmerTextView) findViewById(R.id.tvNoListItem);
			new Shimmer().start(tvNoFavouriteListItem);
			tvNoFavouriteListItem.setTypeface(font);
			tvNoFavouriteListItem.setText("No favourites");

			favouritesListView = (XListView) findViewById(R.id.lvListItems);
			favouritesListView.setPullLoadEnable(false);
			favouritesListView.setXListViewListener(new IXListViewListener() {

				@Override
				public void onRefresh() {
					// TODO Auto-generated method stub
					getFavourites();
					setFavourites();
					favouritesListView.invalidateViews();
					favouritesAdapter.notifyDataSetChanged();
					favouritesListView.stopRefresh();
				}

				@Override
				public void onLoadMore() {
					// TODO Auto-generated method stub

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
															+ "\"\n\n- 'Plain");
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

				if (storyIsClean) {
					if (story.length() > 1) {
						if (story.length() < 1000) {
							publishStory(story);
							emojiconEditText.setText("");
						} else {
							emojiconEditText
									.setError("Try shortening that a bit...");
						}
					} else {
						emojiconEditText.setError("Please say something...");
					}
				} else {
					emojiconEditText
							.setError(getString(R.string.et_not_clean_error));
				}
			}
		});
	}

	private void setUpEmojiKeyboardReplies() {
		// TODO Auto-generated method stub
		emojiconEditTextReplies = (EmojiconEditText) findViewById(R.id.emojicon_edit_text_replies);
		rootViewReplies = findViewById(R.id.root_view_replies);
		emojiconButtonReplies = (ImageView) findViewById(R.id.emoji_btn_replies);
		submitButtonReplies = (ImageView) findViewById(R.id.submit_btn_replies);
		popupReplies = new EmojiconsPopup(rootViewReplies, this);

		popupReplies.setSizeForSoftKeyboard();
		popupReplies
				.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {

					@Override
					public void onEmojiconClicked(Emojicon emojicon) {
						emojiconEditTextReplies.append(emojicon.getEmoji());
					}
				});
		popupReplies
				.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {

					@Override
					public void onEmojiconBackspaceClicked(View v) {
						KeyEvent event = new KeyEvent(0, 0, 0,
								KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
								KeyEvent.KEYCODE_ENDCALL);
						emojiconEditTextReplies.dispatchKeyEvent(event);
					}
				});
		popupReplies.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				changeEmojiKeyboardIcon(emojiconButtonReplies,
						R.drawable.smiley);
			}
		});
		popupReplies
				.setOnSoftKeyboardOpenCloseListener(new OnSoftKeyboardOpenCloseListener() {

					@Override
					public void onKeyboardOpen(int keyBoardHeight) {

					}

					@Override
					public void onKeyboardClose() {
						if (popupReplies.isShowing())
							popupReplies.dismiss();
					}
				});
		popupReplies
				.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {

					@Override
					public void onEmojiconClicked(Emojicon emojicon) {
						emojiconEditTextReplies.append(emojicon.getEmoji());
					}
				});
		popupReplies
				.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {

					@Override
					public void onEmojiconBackspaceClicked(View v) {
						KeyEvent event = new KeyEvent(0, 0, 0,
								KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
								KeyEvent.KEYCODE_ENDCALL);
						emojiconEditTextReplies.dispatchKeyEvent(event);
					}
				});
		emojiconButtonReplies.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!popupReplies.isShowing()) {

					if (popupReplies.isKeyBoardOpen()) {
						popupReplies.showAtBottom();
						changeEmojiKeyboardIcon(emojiconButtonReplies,
								R.drawable.ic_action_keyboard);
					}

					else {
						emojiconEditTextReplies.setFocusableInTouchMode(true);
						emojiconEditTextReplies.requestFocus();
						popupReplies.showAtBottomPending();
						final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						inputMethodManager.showSoftInput(
								emojiconEditTextReplies,
								InputMethodManager.SHOW_IMPLICIT);
						changeEmojiKeyboardIcon(emojiconButtonReplies,
								R.drawable.ic_action_keyboard);
					}
				}

				else {
					popupReplies.dismiss();
				}
			}
		});

		submitButtonReplies.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String story = emojiconEditTextReplies.getText().toString()
						.trim();
				storyIsClean = true;
				storyIsClean = filterWords(story);

				if (storyIsClean) {
					if (story.length() > 1) {
						if (story.length() < 1000) {
							publishStory(story);
							emojiconEditTextReplies
									.setVisibility(View.INVISIBLE);
							emojiconButtonReplies.setVisibility(View.INVISIBLE);
							submitButtonReplies.setVisibility(View.INVISIBLE);
							emojiconEditTextReplies.getText().clear();
						} else {
							emojiconEditTextReplies
									.setError("Try shortening that a bit...");
						}
					} else {
						emojiconEditTextReplies
								.setError("Please say something...");
					}
				} else {
					emojiconEditTextReplies
							.setError(getString(R.string.et_not_clean_error));
				}
			}
		});
	}

	private void changeEmojiKeyboardIcon(ImageView iconToBeChanged,
			int drawableResourceId) {
		iconToBeChanged.setImageResource(drawableResourceId);
	}

	protected void searchForTag(String tagQuery) {
		// TODO Auto-generated method stub
		setSupportProgressBarIndeterminateVisibility(true);
		Toast.makeText(getApplicationContext(), "Just a moment",
				Toast.LENGTH_SHORT).show();

		String key = "tag";
		Query query = QueryBuilder.build(key, tagQuery, Operator.LIKE);
		HashMap<String, String> metaHeaders = new HashMap<String, String>();
		metaHeaders.put("orderByDescending", "_$createdAt");
		storageService.setOtherMetaHeaders(metaHeaders);
		storageService.findDocumentsByQuery(getString(R.string.database_name),
				getString(R.string.collection_name), query,
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

						queryResultStories.clear();
						queryResultLikes.clear();
						queryResultTags.clear();
						queryResultAdmins.clear();
						queryResults.clear();

						for (int i = 0; i < jsonDocArray.size(); i++) {
							try {
								JSONObject json = new JSONObject(jsonDocArray
										.get(i));
								queryResultStories.add(json.getString("story"));
								queryResultLikes.add(json.getInt("likes"));
								queryResultTags.add(json.getString("tag"));
								queryResultAdmins.add(json.getBoolean("admin"));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						for (int i = 0; i < queryResultStories.size(); i++) {
							queryResults.add(new ListItem(queryResultStories
									.get(i), queryResultLikes.get(i),
									queryResultTags.get(i), queryResultAdmins
											.get(i)));
						}

						runOnUiThread(new Runnable() {
							public void run() {
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
							}
						});

						populateReplies(queryResults);
					}

					public void onException(Exception ex) {
						System.out.println("Exception Message"
								+ ex.getMessage());
						errorHandler(ex);
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

	private void getStories() {
		// TODO Auto-generated method stub
		setSupportProgressBarIndeterminateVisibility(true);

		HashMap<String, String> metaHeaders = new HashMap<String, String>();
		metaHeaders.put("orderByDescending", "_$createdAt");
		storageService.setOtherMetaHeaders(metaHeaders);
		storageService.findAllDocuments(getString(R.string.database_name),
				getString(R.string.collection_name), new App42CallBack() {
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

	private void getStoriesForReplies(final ArrayList<String> storedTags) {
		// TODO Auto-generated method stub
		setSupportProgressBarIndeterminateVisibility(true);

		HashMap<String, String> metaHeaders = new HashMap<String, String>();
		metaHeaders.put("orderByDescending", "_$createdAt");
		storageService.setOtherMetaHeaders(metaHeaders);
		storageService.findAllDocuments(getString(R.string.database_name),
				getString(R.string.collection_name), new App42CallBack() {
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

						extractReplies(jsonDocArray, storedTags, jsonIdArray);
					}

					public void onException(Exception ex) {
						System.out.println("Exception Message"
								+ ex.getMessage());
						errorHandler(ex);
					}
				});
	}

	private void populateList(final ArrayList<String> jsonDocArray,
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

				listView.stopRefresh();
				listView.stopLoadMore();
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
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

						try {
							if (new JSONObject(jsonDocArray.get(arg2 - 1))
									.getBoolean("admin") == true
									&& new JSONObject(jsonDocArray
											.get(arg2 - 1))
											.getString("story")
											.contains("update on the PlayStore")) {
								String url = getString(R.string.playstore_link);
								Intent i = new Intent(Intent.ACTION_VIEW);
								i.setData(Uri.parse(url));
								startActivity(i);
							}
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

										emojiconEditText.setText("@" + tag
												+ " ");
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
											socDialog.dismiss();
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

	private void fetchMoreStories() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "Getting more plains...",
				Toast.LENGTH_SHORT).show();
		setSupportProgressBarIndeterminateVisibility(true);

		HashMap<String, String> metaHeaders = new HashMap<String, String>();
		metaHeaders.put("orderByDescending", "_$createdAt");
		storageService.setOtherMetaHeaders(metaHeaders);
		storageService.findAllDocuments(getString(R.string.database_name),
				getString(R.string.collection_name), 100, offset,
				new App42CallBack() {
					public void onSuccess(Object response) {
						Storage storage = (Storage) response;

						ArrayList<Storage.JSONDocument> jsonDocList = storage
								.getJsonDocList();

						appendJsonDocArray = new ArrayList<String>();
						appendJsonIdArray = new ArrayList<String>();

						for (int i = 0; i < jsonDocList.size(); i++) {
							appendJsonDocArray.add(jsonDocList.get(i)
									.getJsonDoc());
							appendJsonIdArray
									.add(jsonDocList.get(i).getDocId());
						}

						appendDataToList(appendJsonDocArray, appendJsonIdArray);

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								offset += 100;
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

	protected void appendDataToList(final ArrayList<String> appendJsonDocArray,
			final ArrayList<String> appendJsonIdArray) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				jsonDocArray.addAll(appendJsonDocArray);
				jsonIdArray.addAll(appendJsonIdArray);
				populateList(jsonDocArray, jsonIdArray);
				setSupportProgressBarIndeterminateVisibility(false);
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
			String tag = RandomStringUtils.random(3, true, true);
			jsonStory.put("story", story);
			jsonStory.put("likes", 0);
			jsonStory.put("tag", tag.toLowerCase());
			jsonStory.put("admin", false);

			storeTag(tag);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		storageService.insertJSONDocument(getString(R.string.database_name),
				getString(R.string.collection_name), jsonStory,
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
								getStories();
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
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								setSupportProgressBarIndeterminateVisibility(false);
								Toast.makeText(getApplicationContext(),
										"Liked!", Toast.LENGTH_SHORT).show();
								getStories();
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

	private void extractReplies(final ArrayList<String> jsonDocArray,
			final ArrayList<String> storedTags,
			final ArrayList<String> jsonIdArray) {
		// TODO Auto-generated method stub
		fetchedTagStories.clear();
		replies.clear();

		for (int i = 0; i < jsonDocArray.size(); i++) {
			try {
				JSONObject json = new JSONObject(jsonDocArray.get(i));
				fetchedTagStories.add(json.getString("story"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (int i = 0; i < storedTags.size(); i++) {
			for (int j = 0; j < 100; j++) {
				if (fetchedTagStories.get(j).contains(
						"@" + storedTags.get(i).toLowerCase())) {
					try {
						JSONObject json = new JSONObject(jsonDocArray.get(j));
						replies.add(new ListItem(json.getString("story"), json
								.getInt("likes"), json.getString("tag"), json
								.getBoolean("admin")));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		populateReplies(replies);
	}

	private void populateReplies(final ArrayList<ListItem> replies) {
		// TODO Auto-generated method stub
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				setSupportProgressBarIndeterminateVisibility(false);
				if (replies != null && replies.size() > 0) {
					tvNoReplyListItem.setVisibility(View.INVISIBLE);
				} else {
					tvNoReplyListItem.setVisibility(View.VISIBLE);
					tvNoReplyListItem.setText("No replies");
				}

				activity.runOnUiThread(new Runnable() {
					public void run() {
						repliesAdapter = new ListItemAdapter(
								getApplicationContext(), R.layout.list_item,
								replies);
						SwingBottomInAnimationAdapter swing = new SwingBottomInAnimationAdapter(
								repliesAdapter);
						swing.setAbsListView(repliesListView);
						repliesListView.setAdapter(swing);

						repliesListView.stopRefresh();
						repliesListView
								.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
										// TODO Auto-generated method stub
										emojiconEditTextReplies
												.setVisibility(View.VISIBLE);
										emojiconButtonReplies
												.setVisibility(View.VISIBLE);
										submitButtonReplies
												.setVisibility(View.VISIBLE);
										emojiconEditTextReplies.setText("@"
												+ replies.get(arg2 - 1)
														.getTag().toLowerCase()
												+ " ");
									}
								});

						repliesListView
								.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

									@Override
									public boolean onItemLongClick(
											AdapterView<?> arg0, View arg1,
											final int arg2, long arg3) {
										// TODO Auto-generated method stub
										rocDialog = new ReplyOptionsCustomDialog(
												activity);
										rocDialog
												.getWindow()
												.setBackgroundDrawable(
														new ColorDrawable(
																Color.TRANSPARENT));
										rocDialog.show();
										rocDialog.share
												.setOnClickListener(new View.OnClickListener() {

													@Override
													public void onClick(View v) {
														// TODO Auto-generated
														// method
														// stub
														i = new Intent(
																android.content.Intent.ACTION_SEND);
														i.setType("text/plain");
														i.putExtra(
																android.content.Intent.EXTRA_TEXT,
																"\""
																		+ replies
																				.get(arg2 - 1)
																				.getStory()
																		+ "\"\n\n- story from 'Plain");
														startActivity(Intent
																.createChooser(
																		i,
																		"Share the story using..."));
													}
												});
										rocDialog.favourite
												.setOnClickListener(new View.OnClickListener() {

													@Override
													public void onClick(View v) {
														// TODO Auto-generated
														// method
														// stub
														String story = replies
																.get(arg2 - 1)
																.getStory();
														int likes = replies
																.get(arg2 - 1)
																.getLikes();
														String tag = replies
																.get(arg2 - 1)
																.getTag();
														boolean admin = replies
																.get(arg2 - 1)
																.isAdmin();

														favouriteStory(story,
																likes, tag,
																admin);
														rocDialog.dismiss();
													}
												});
										return false;
									}
								});
					}
				});
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
					listView.stopRefresh();
					listView.stopLoadMore();
					tvNoListItem.setVisibility(View.VISIBLE);
					tvNoListItem.setText("Refresh");
					tvNoListItem.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							getStories();
						}
					});

					repliesListView.stopRefresh();
					tvNoReplyListItem.setVisibility(View.VISIBLE);
					tvNoReplyListItem.setText("Refresh");
					tvNoReplyListItem
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									getStoriesForReplies(getTags());
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
		mPager.setCurrentItem(0);
		mPager.setOffscreenPageLimit(3);

		mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.plain_menu, menu);

		SubMenu subMenu = menu.addSubMenu("Options");
		subMenu.add(0, 0, 0, "Menu:");
		subMenu.add(1, 1, 1, "Tribes");
		subMenu.add(2, 2, 2, "Rules");
		subMenu.add(4, 4, 4, "Invite");
		subMenu.add(5, 5, 5, "About");

		MenuItem subMenuItem = subMenu.getItem();
		subMenuItem.setIcon(R.drawable.more_menu_icon);
		subMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		try {
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			SearchView searchView = (SearchView) menu.findItem(R.id.mSearch)
					.getActionView();

			if (searchView != null) {
				searchView.setSearchableInfo(searchManager
						.getSearchableInfo(getComponentName()));
				searchView.setIconifiedByDefault(true);
				searchView.setIconified(true);
				searchView.setQueryHint(Html
						.fromHtml("<font color = #ffffff>Keyword...</font>"));
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

		String key = "story";

		Query query = QueryBuilder.build(key, queryString, Operator.LIKE);

		HashMap<String, String> metaHeaders = new HashMap<String, String>();
		metaHeaders.put("orderByDescending", "_$createdAt");
		storageService.setOtherMetaHeaders(metaHeaders);
		storageService.findDocumentsByQuery(getString(R.string.database_name),
				getString(R.string.collection_name), query,
				new App42CallBack() {
					public void onSuccess(Object response) {
						Storage storage = (Storage) response;
						jsonDocArray = new ArrayList<String>();
						jsonIdArray = new ArrayList<String>();
						ArrayList<Storage.JSONDocument> jsonDocList = storage
								.getJsonDocList();
						for (int i = 0; i < jsonDocList.size(); i++) {
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
		case 1:
			i = new Intent(getApplicationContext(), Tribes.class);
			startActivity(i);
			break;
		case 2:
			i = new Intent(getApplicationContext(), Rules.class);
			startActivity(i);
			break;
		case 3:
			i = new Intent(android.content.Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(android.content.Intent.EXTRA_TEXT,
					getString(R.string.share_message));
			startActivity(Intent.createChooser(i, "Invite friends using..."));
			break;
		case 4:
			i = new Intent(getApplicationContext(), About.class);
			startActivity(i);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (emojiconEditTextReplies.getVisibility() == View.VISIBLE) {
			emojiconEditTextReplies.setVisibility(View.INVISIBLE);
			emojiconButtonReplies.setVisibility(View.INVISIBLE);
			submitButtonReplies.setVisibility(View.INVISIBLE);
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
}
