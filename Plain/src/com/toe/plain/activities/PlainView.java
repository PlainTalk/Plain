package com.toe.plain.activities;

import github.ankushsachdeva.emojicon.EmojiconTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
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
import com.toe.plain.R;
import com.toe.plain.adapters.ListItemAdapter;
import com.toe.plain.classes.ShimmerTextView;
import com.toe.plain.classes.XListView;
import com.toe.plain.classes.XListView.IXListViewListener;
import com.toe.plain.dialogs.TagTextDialog;
import com.toe.plain.listitems.ListItem;
import com.toe.plain.utils.TextViewFixTouchConsume;
import com.toe.plain.utils.TimeUtils;

public class PlainView extends SherlockFragmentActivity {

	Intent i;
	String plain, tag, timestamp, error;
	Integer likes;
	EmojiconTextView tvPlain;
	TextView tvTag, tvLikes, tvTimestamp, tvNoListItem;
	XListView lvReplies;
	SherlockFragmentActivity activity;
	ListItemAdapter adapter;

	ArrayList<String> queryResultStories = new ArrayList<String>();
	ArrayList<Integer> queryResultLikes = new ArrayList<Integer>();
	ArrayList<String> queryResultTags = new ArrayList<String>();
	ArrayList<Boolean> queryResultAdmins = new ArrayList<Boolean>();

	ArrayList<String> jsonDocArray, jsonIdArray, jsonTimesArray;

	StorageService storageService;
	ArrayList<ListItem> queryResults = new ArrayList<ListItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plain_view);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		initialize();
		setUp();
		getBundle();
	}

	private void initialize() {
		// TODO Auto-generated method stub
		App42API.initialize(getApplicationContext(),
				getString(R.string.api_key), getString(R.string.secret_key));
		App42CacheManager.setPolicy(Policy.NETWORK_FIRST);
		storageService = App42API.buildStorageService();
	}

	private void setUp() {
		// TODO Auto-generated method stub
		Typeface font = Typeface.createFromAsset(getAssets(),
				getString(R.string.list_font));

		activity = this;

		tvPlain = (EmojiconTextView) findViewById(R.id.tvPlain);
		tvPlain.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod
				.getInstance());
		tvPlain.setFocusable(false);
		tvPlain.setClickable(false);
		tvPlain.setLongClickable(false);
		tvPlain.setTypeface(font);

		tvTag = (TextView) findViewById(R.id.tvTag);
		tvLikes = (TextView) findViewById(R.id.tvLikes);
		tvTimestamp = (TextView) findViewById(R.id.tvTimestamp);
		tvNoListItem = (TextView) findViewById(R.id.tvNoListItem);

		lvReplies = (XListView) findViewById(R.id.lvListItems);
	}

	private void getBundle() {
		// TODO Auto-generated method stub
		Bundle b = getIntent().getExtras();
		plain = b.getString("plain");
		tag = b.getString("tag");
		likes = b.getInt("likes");
		timestamp = b.getString("time");

		searchForTag("@" + tag);
		populateViews();
	}

	private void populateViews() {
		// TODO Auto-generated method stub
		tvPlain.setText(addClickablePart("\"" + plain + "\""),
				TextView.BufferType.SPANNABLE);
		tvTag.setText(tag);
		tvLikes.setText(likes + "");
		tvTimestamp.setText(processTime(timestamp));

		getSupportActionBar().setTitle("@" + tag);
	}

	private String processTime(String timestamp) {
		// TODO Auto-generated method stub
		Date date = formatTime(timestamp);
		long time = date.getTime();

		Date curDate = currentDate();
		long now = curDate.getTime();
		if (time > now || time <= 0) {
			return null;
		}

		Calendar calender = Calendar.getInstance();
		TimeZone timezone = calender.getTimeZone();

		int timeDistance = getTimeDistanceInMilliseconds(time
				+ timezone.getRawOffset());

		return TimeUtils.millisToLongDHMS(timeDistance);
	}

	@SuppressWarnings("deprecation")
	private Date formatTime(String rawTime) {
		// TODO Auto-generated method stub
		String spitDate[] = rawTime.split("T");
		String sDate = spitDate[0];
		String dateSplit[] = sDate.split("-");
		String sYear = dateSplit[0];
		String sMonth = dateSplit[1];
		String sDay = dateSplit[2];

		int month = Integer.parseInt(sMonth);
		int day = Integer.parseInt(sDay);

		String splitTime[] = rawTime.split(":");
		String rawHour = splitTime[0];
		String hour = rawHour.substring(rawHour.length() - 2, rawHour.length());
		String minute = splitTime[1];

		int hourInt = Integer.parseInt(hour);

		String hourString = hourInt + "";
		if (hourString.length() == 1) {
			hourString = 0 + hourString;
		}

		Date dateObj = new Date(Integer.parseInt(sYear.substring(2)) + 100,
				month - 1, day, hourInt, Integer.parseInt(minute));
		return dateObj;
	}

	public static Date currentDate() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime();
	}

	private static int getTimeDistanceInMilliseconds(long time) {
		long timeDistance = currentDate().getTime() - time;
		return Math.round(Math.abs(timeDistance));
	}

	protected void searchForTag(String tagQuery) {
		// TODO Auto-generated method stub
		setSupportProgressBarIndeterminateVisibility(true);

		String key = "story";
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
						jsonTimesArray = new ArrayList<String>();

						for (int i = 0; i < jsonDocList.size(); i++) {
							jsonDocArray.add(jsonDocList.get(i).getJsonDoc());
							jsonIdArray.add(jsonDocList.get(i).getDocId());
							jsonTimesArray.add(jsonDocList.get(i)
									.getCreatedAt());
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
											.get(i), jsonTimesArray.get(i)));
						}

						runOnUiThread(new Runnable() {
							public void run() {
								if (jsonDocArray.size() > 1) {
									Toast.makeText(
											activity,
											"Found " + jsonDocArray.size()
													+ " plains!",
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(activity, "Found a plain!",
											Toast.LENGTH_SHORT).show();
								}
							}
						});

						populateListView(queryResults);
					}

					public void onException(Exception ex) {
						System.out.println("Exception Message"
								+ ex.getMessage());
						errorHandler(ex);
					}
				});
	}

	protected void populateListView(final ArrayList<ListItem> queryResults) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			public void run() {
				tvNoListItem.setText("Replies");
				lvReplies = (XListView) findViewById(R.id.lvListItems);
				lvReplies.setPullLoadEnable(false);
				lvReplies.stopRefresh();

				if (adapter == null) {
					adapter = new ListItemAdapter(getApplicationContext(),
							activity, R.layout.list_item, queryResults);
					SwingBottomInAnimationAdapter swing = new SwingBottomInAnimationAdapter(
							adapter);
					swing.setAbsListView(lvReplies);
					lvReplies.setAdapter(swing);
				} else {
					adapter.notifyDataSetChanged();
					SwingBottomInAnimationAdapter swing = new SwingBottomInAnimationAdapter(
							adapter);
					swing.setAbsListView(lvReplies);
				}

				setSupportProgressBarIndeterminateVisibility(false);
				lvReplies.setXListViewListener(new IXListViewListener() {

					@Override
					public void onRefresh() {
						// TODO Auto-generated method stub
						searchForTag(tag);
					}

					@Override
					public void onLoadMore() {
						// TODO Auto-generated method stub

					}
				});
			}
		});
	}

	private SpannableStringBuilder addClickablePart(String str) {
		SpannableStringBuilder ssb = new SpannableStringBuilder(str);

		if (!str.contains("rp@")) {
			int idx1 = str.indexOf("@");
			int idx2 = 0;
			while (idx1 != -1) {
				idx2 = idx1 + 4;
				final String clickString = str.substring(idx1, idx2);
				ssb.setSpan(new TextClickableSpan(clickString), idx1, idx2, 0);
				idx1 = str.indexOf("@", idx2);
			}
		} else {
			int idx1 = str.indexOf("@", str.indexOf("@") + 1);
			int idx2 = 0;
			while (idx1 != -1) {
				idx2 = idx1 + 4;
				final String clickString = str.substring(idx1, idx2);
				ssb.setSpan(new TextClickableSpan(clickString), idx1, idx2, 0);
				idx1 = str.indexOf("@", idx2);
			}
		}

		return ssb;
	}

	class TextClickableSpan extends ClickableSpan {
		String clicked;

		public TextClickableSpan(String string) {
			// TODO Auto-generated constructor stub
			super();
			clicked = string;
		}

		public void onClick(View tv) {
			TagTextDialog ttcDialog = new TagTextDialog(activity);
			ttcDialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			ttcDialog.collection = getResources().getString(
					R.string.collection_name);
			ttcDialog.tag = clicked.replaceAll("@", "");
			ttcDialog.show();
		}

		public void updateDrawState(TextPaint ds) {
			ds.setUnderlineText(false);
			ds.setColor(Color.rgb(12, 98, 120));
		}
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
					lvReplies.stopRefresh();
					lvReplies.stopLoadMore();
					tvNoListItem.setVisibility(View.VISIBLE);
					tvNoListItem.setText("Refresh");
					tvNoListItem.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							searchForTag(tag);
						}
					});
				}
			});
		} else if (ex.getMessage().contains("No document")) {
			error = "No replies found :-(";
		} else if (ex.getMessage().contains("UnAuthorized Access")) {
			error = "Hi, how are you? Please try again in a few minutes :-)";
		} else {
			error = ex.getMessage();
		}

		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				setSupportProgressBarIndeterminateVisibility(false);
				tvNoListItem.setText(error);
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

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

}
